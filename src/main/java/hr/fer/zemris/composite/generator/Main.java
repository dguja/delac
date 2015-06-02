package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.exception.GeneratorException;
import hr.fer.zemris.composite.generator.exception.ParseException;
import hr.fer.zemris.composite.generator.model.Dataset;
import hr.fer.zemris.composite.generator.model.DirectionType;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.random.RandomProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class Main {

  private static String CONFIG_FILE = "dataset.json";

  private static String OUTPUT_DIRECTORY = "other/output/";

  private static String EXTENSION = "dot";

  private static String OUTPUT_DOT_FILE_FORMAT = OUTPUT_DIRECTORY + "graph%d." + EXTENSION;

  private static String OUTPUT_PNG_FILE_FORMAT = OUTPUT_DIRECTORY + "graph%d.png";

  private static String HTML_IMG_FORMAT = "output/graph%d.png";

  private static String HTML_FILE = "other/output.html";

  private static String DOT_NAME = "dot";

  private static String DOT_IMG_TYPE = "-Tpng";

  private static final String GRAPH_NAME = "model";

  public static void main(final String[] args) throws IOException {
    final long millis = 1433259412646L;//System.currentTimeMillis();
    RandomProvider.getRandom().setSeed(millis);

    System.err.println("Using generator seed: " + millis + ".");

    final ConfigParser parser = parseConfiguration();

    final ModelGenerator generator =
        new ModelGenerator(parser.getModelCount(), parser.getCopyInputs(), parser.getDiscreteDistributions(),
            parser.getRealDistributions());

    final Dataset dataset = generateDataset(generator);

    System.err.println("Calculating reliabilities...");

    for (final Model model : dataset.getModels()) {
      model.getOutput().calculateReliability(DirectionType.PARENT);
    }

    convert(dataset);

    System.err.println("Generating HTML output \"" + HTML_FILE + "\"...");

    Files.write(Paths.get(HTML_FILE), IOUtilities.generateHtml(dataset.getModels().size(), HTML_IMG_FORMAT).getBytes());
    
    System.err.println("Done.");
  }

  private static ConfigParser parseConfiguration() throws IOException, FileNotFoundException {
    final ConfigParser parser = new ConfigParser();

    System.err.println("Parsing configuration file \"" + CONFIG_FILE + "\"...");

    try (final InputStream inputStream = new FileInputStream(CONFIG_FILE)) {
      parser.parse(inputStream);
    } catch (final ParseException exception) {
      exit(exception.getMessage());
    }

    return parser;
  }

  private static Dataset generateDataset(final ModelGenerator generator) {
    System.err.println("Generating dataset...");

    try {
      return generator.generate();
    } catch (final GeneratorException exception) {
      exit(exception.getMessage());
    }

    return null;
  }

  private static int convert(final Dataset dataset) throws IOException {
    System.err.println("Generating images...");

    deleteFileType(OUTPUT_DIRECTORY, "dot");

    final List<Process> processes = new ArrayList<>();

    final int modelCount = dataset.getModels().size();
    for (int i = 0; i < modelCount; i++) {
      final String dotFile = String.format(OUTPUT_DOT_FILE_FORMAT, i);
      final String pngFile = String.format(OUTPUT_PNG_FILE_FORMAT, i);

      Files.write(Paths.get(dotFile), IOUtilities.toDot(dataset.getModels().get(i), GRAPH_NAME).getBytes());

      System.err.println(DOT_NAME + " " + DOT_IMG_TYPE + " " + dotFile + " > " + pngFile);

      final ProcessBuilder builder = new ProcessBuilder(DOT_NAME, DOT_IMG_TYPE, dotFile);
      builder.redirectOutput(new File(pngFile));

      processes.add(builder.start());
    }

    for (final Process process : processes) {
      try {
        process.waitFor();
      } catch (final InterruptedException exception) {}
    }

    return modelCount;
  }

  private static void deleteFileType(final String directory, final String extension) throws IOException {
    Files.walkFileTree(Paths.get(directory), new FileVisitor<Path>() {

      @Override
      public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.SKIP_SUBTREE;
      }

      @Override
      public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        if (file.getFileName().toString().endsWith("." + extension)) {
          Files.deleteIfExists(file);
        }

        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
      }

    });
  }

  private static void exit(final String message) {
    System.err.println(message);
    System.exit(1);
  }
  
}
