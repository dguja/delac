package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.exception.GeneratorException;
import hr.fer.zemris.composite.generator.exception.ParseException;
import hr.fer.zemris.composite.generator.model.Dataset;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

public class Main {

  private static String CONFIG_FILE = "conf.json";

  private static String OUTPUT_FILE = "output";

  public static void main(final String[] args) throws IOException {
    final ConfigParser parser = new ConfigParser();

    try (final InputStream inputStream = new FileInputStream(CONFIG_FILE)) {
      parser.parse(inputStream);
    } catch (final ParseException exception) {
      exit(exception.getMessage());
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

    try (final ObjectOutputStream objectStream = new ObjectOutputStream(new FileOutputStream(OUTPUT_FILE))) {
      objectStream.writeObject(dataset);
    }
  }

  private static void exit(final String message) {
    System.err.println(message);
    System.exit(1);
  }

}
