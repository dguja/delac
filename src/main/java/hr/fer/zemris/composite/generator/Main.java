package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.dot.DotUtilities;
import hr.fer.zemris.composite.generator.exception.GeneratorException;
import hr.fer.zemris.composite.generator.exception.ParseException;
import hr.fer.zemris.composite.generator.model.Dataset;
import hr.fer.zemris.composite.generator.model.DirectionType;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.random.RandomProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

  private static String CONFIG_FILE = "conf.json";

  private static String OUTPUT_FILE_FORMAT = "other/output/graph%d.dot";

  public static void main(final String[] args) throws IOException {
    final long millis = 1424108553902l; // System.currentTimeMillis();

    System.err.println(millis);
    RandomProvider.getRandom().setSeed(millis);

    final ConfigParser parser = new ConfigParser();

    try (final InputStream inputStream = new FileInputStream(CONFIG_FILE)) {
      parser.parse(inputStream);
    } catch (final ParseException exception) {
      exception.printStackTrace();
      exit("");
    }

    final ModelGenerator generator =
        new ModelGenerator(parser.getModelCount(), parser.getCopyInputs(), parser.getDiscreteDistributions(),
            parser.getRealDistributions());

    Dataset dataset = null;
    try {
      dataset = generator.generate();
    } catch (final GeneratorException exception) {
      exit(exception.getMessage());
    }

    for (int i = 0; i < dataset.getModels().size(); i++) {
      final Model model = dataset.getModels().get(i);
      model.getOutput().calculateReliability(DirectionType.PARENT);

      final String filename = String.format(OUTPUT_FILE_FORMAT, i);
      Files.write(Paths.get(filename), DotUtilities.toDot(model, "test").getBytes());
    }
  }

  private static void exit(final String message) {
    System.err.println(message);
    System.exit(1);
  }

  // private static long id = 0;
  //
  // private static Model createModel() {
  // final List<InputNode> inputs = new ArrayList<>();
  //
  // for (int i = 0; i < 6; i++) {
  // inputs.add(new InputNode(nextId(), (i / 6.)));
  // }
  //
  // final SequenceNode sn1 = new SequenceNode(nextId(), 1);
  // sn1.addParent(inputs.get(1));
  // final SequenceNode sn2 = new SequenceNode(nextId(), 1);
  // sn2.addParent(inputs.get(3));
  // sn2.addParent(inputs.get(4));
  // final SequenceNode sn3 = new SequenceNode(nextId(), 1);
  // sn3.addParent(inputs.get(4));
  // sn3.addParent(inputs.get(5));
  //
  // final SequenceNode sn4 = new SequenceNode(nextId(), 2);
  // sn4.addParent(inputs.get(0));
  // final SequenceNode sn5 = new SequenceNode(nextId(), 2);
  // sn5.addParent(inputs.get(0));
  // final SequenceNode sn6 = new SequenceNode(nextId(), 2);
  // sn6.addParent(sn1);
  // sn6.addParent(inputs.get(2));
  // sn6.addParent(sn2);
  // final SequenceNode sn7 = new SequenceNode(nextId(), 2);
  // sn7.addParent(sn3);
  // final SequenceNode sn8 = new SequenceNode(nextId(), 2);
  // sn8.addParent(sn3);
  //
  // final SequenceNode sn9 = new SequenceNode(nextId(), 3);
  // sn9.addParent(sn4);
  // sn9.addParent(sn6);
  // sn9.addParent(sn7);
  // final SequenceNode sn10 = new SequenceNode(nextId(), 3);
  // sn10.addParent(sn5);
  // final SequenceNode sn11 = new SequenceNode(nextId(), 3);
  // sn11.addParent(sn6);
  // sn11.addParent(sn7);
  // final SequenceNode sn12 = new SequenceNode(nextId(), 3);
  // sn12.addParent(sn8);
  // final SequenceNode sn13 = new SequenceNode(nextId(), 3);
  // sn13.addParent(sn3);
  //
  // final OutputNode output = new OutputNode(nextId(), 4);
  // output.addParent(sn9);
  // output.addParent(sn10);
  // output.addParent(sn11);
  // output.addParent(sn12);
  // output.addParent(sn13);
  //
  // return new Model(inputs, output);
  //
  // }
  //
  // private static Long nextId() {
  // return id++;
  // }

}
