package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.dot.DotUtilities;
import hr.fer.zemris.composite.generator.exception.GeneratorException;
import hr.fer.zemris.composite.generator.exception.ParseException;
import hr.fer.zemris.composite.generator.model.Dataset;
import hr.fer.zemris.composite.generator.model.DirectionType;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.model.nodes.InputNode;
import hr.fer.zemris.composite.generator.model.nodes.LoopNode;
import hr.fer.zemris.composite.generator.model.nodes.OutputNode;
import hr.fer.zemris.composite.generator.model.nodes.SequenceNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {

//  private static String CONFIG_FILE = "conf.json";

  public static void main(final String[] args) throws IOException {
//    final ConfigParser parser = new ConfigParser();
//
//    try (final InputStream inputStream = new FileInputStream(CONFIG_FILE)) {
//      parser.parse(inputStream);
//    } catch (final ParseException exception) {
//      exit(exception.getMessage());
//    }
//
//    final ModelGenerator generator =
//        new ModelGenerator(parser.getModelCount(), parser.getCopyInputs(), parser.getDiscreteDistributions(),
//            parser.getRealDistributions());
//
//    Dataset dataset = null;
//    try {
//      dataset = generator.generate();
//    } catch (final GeneratorException exception) {
//      exit(exception.getMessage());
//    }
//
//    final Model model = dataset.getModels().get(0);
    Model model = createModel();
    model.getOutput().calculateReliability(DirectionType.PARENT);

    System.out.println(DotUtilities.toDot(model, "test"));
  }

//  private static void exit(final String message) {
//    System.err.println(message);
//    System.exit(1);
//  }

   private static long id = 0;
  
   private static Model createModel() {
   final List<InputNode> inputs = new ArrayList<>();
  
   for (int i = 0; i < 5; i++) {
   inputs.add(new InputNode(nextId(), ((i + 1) / 6.)));
   }
  
   SequenceNode sn1 = new SequenceNode(nextId(), 1);
   SequenceNode sn2 = new SequenceNode(nextId(), 1);
   
   LoopNode loop = new LoopNode(nextId(), 1);
   
   sn1.addParent(inputs.get(0));
   sn1.addParent(inputs.get(1));
   
   sn2.addParent(inputs.get(2));
   sn2.addParent(inputs.get(3));
   
   loop.addParent(inputs.get(4));
   loop.setNumRepetitions(2);
   
   OutputNode output = new OutputNode(nextId(), 2);
   output.addParent(sn1);
   output.addParent(sn2);
   output.addParent(loop);
   
//   final SequenceNode sn1 = new SequenceNode(nextId(), 1);
//   sn1.addParent(inputs.get(1));
//   final SequenceNode sn2 = new SequenceNode(nextId(), 1);
//   sn2.addParent(inputs.get(3));
//   sn2.addParent(inputs.get(4));
//   final SequenceNode sn3 = new SequenceNode(nextId(), 1);
//   sn3.addParent(inputs.get(4));
//   sn3.addParent(inputs.get(5));
//  
//   final SequenceNode sn4 = new SequenceNode(nextId(), 2);
//   sn4.addParent(inputs.get(0));
//   final SequenceNode sn5 = new SequenceNode(nextId(), 2);
//   sn5.addParent(inputs.get(0));
//   final SequenceNode sn6 = new SequenceNode(nextId(), 2);
//   sn6.addParent(sn1);
//   sn6.addParent(inputs.get(2));
//   sn6.addParent(sn2);
//   final SequenceNode sn7 = new SequenceNode(nextId(), 2);
//   sn7.addParent(sn3);
//   final SequenceNode sn8 = new SequenceNode(nextId(), 2);
//   sn8.addParent(sn3);
//  
//   final SequenceNode sn9 = new SequenceNode(nextId(), 3);
//   sn9.addParent(sn4);
//   sn9.addParent(sn6);
//   sn9.addParent(sn7);
//   final SequenceNode sn10 = new SequenceNode(nextId(), 3);
//   sn10.addParent(sn5);
//   final SequenceNode sn11 = new SequenceNode(nextId(), 3);
//   sn11.addParent(sn6);
//   sn11.addParent(sn7);
//   final SequenceNode sn12 = new SequenceNode(nextId(), 3);
//   sn12.addParent(sn8);
//   final SequenceNode sn13 = new SequenceNode(nextId(), 3);
//   sn13.addParent(sn3);
//  
//   final OutputNode output = new OutputNode(nextId(), 4);
//   output.addParent(sn9);
//   output.addParent(sn10);
//   output.addParent(sn11);
//   output.addParent(sn12);
//   output.addParent(sn13);
  
   return new Model(inputs, output);
  
   }
  
   private static Long nextId() {
   return id++;
   }

}
