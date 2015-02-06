package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.parser.ConfigParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

  private static String CONFIG_FILE = "config.json";

  public static void main(String[] args) throws IOException {
    Path path = Paths.get(CONFIG_FILE);
    String json = new String (Files.readAllBytes(path), StandardCharsets.UTF_8);
    
    ModelGenerator generator = ConfigParser.parse(json);
    
  }
}
