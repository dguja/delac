package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.OutputUtilities;
import hr.fer.zemris.composite.generator.exception.GeneratorException;
import hr.fer.zemris.composite.generator.exception.ParseException;
import hr.fer.zemris.composite.generator.model.Dataset;
import hr.fer.zemris.composite.generator.model.DirectionType;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.random.RandomProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class Main {

  private static String CONFIG_FILE = "conf.json";

  private static String OUTPUT_FOLDER = "other/output/";
  
  private static String OUTPUT_DOT_FILE_FORMAT = OUTPUT_FOLDER + "graph%d.dot";
  
  private static String OUTPUT_PNG_FILE_FORMAT = "output/graph%d.png";

  private static String HTML_FILE = "other/output.html";

  
  public static void main(final String[] args) throws IOException {
    final long millis = System.currentTimeMillis();

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

    // delete old .dot files
    Path outputFolder = Paths.get(OUTPUT_FOLDER);
    Files.walkFileTree(outputFolder, new FileVisitor<Path>() {

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
          throws IOException {
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.getFileName().toString().endsWith(".dot")){
          Files.deleteIfExists(file);
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
      }
      
    });
    
    
    for (int i = 0; i < dataset.getModels().size(); i++) {
      final Model model = dataset.getModels().get(i);
      model.getOutput().calculateReliability(DirectionType.PARENT);

      final String dotFile = String.format(OUTPUT_DOT_FILE_FORMAT, i);
      Files.write(Paths.get(dotFile), OutputUtilities.toDot(model, "test").getBytes());
    }
    
    final String htmlFile = String.format(HTML_FILE);
    Files.write(Paths.get(htmlFile), OutputUtilities.toHtml(dataset.getModels().size(), OUTPUT_PNG_FILE_FORMAT).getBytes());
    
  }

  private static void exit(final String message) {
    System.err.println(message);
    System.exit(1);
  }

}
