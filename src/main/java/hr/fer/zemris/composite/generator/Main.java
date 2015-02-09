package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.model.Dataset;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

  private static String CONFIG_FILE = "conf.json";

  private static String OUTPUT_FILE = "output";

  public static void main(final String[] args) throws IOException {
    final Path path = Paths.get(CONFIG_FILE);
    final String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

    final ModelGenerator generator = ConfigParser.parse(json);

    final Dataset dataset = generator.generate();

    final ObjectOutputStream objectStream =
        new ObjectOutputStream(new FileOutputStream(Paths.get(OUTPUT_FILE).toFile()));

    objectStream.writeObject(dataset);
    objectStream.close();
  }

}
