package io.github.isagroup.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

import io.github.isagroup.exceptions.PricingParsingException;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.parsing.PricingManagerParser;
import io.github.isagroup.services.serializer.PricingManagerSerializer;

import io.github.isagroup.services.updaters.Version;
import io.github.isagroup.services.updaters.YamlUpdater;
import io.github.isagroup.services.yaml.SkipNullRepresenter;

public class VersionTransformer {

    private final Yaml yaml;
    private final PricingManagerSerializer serializer;

    public VersionTransformer() {

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        this.yaml = new Yaml(new SkipNullRepresenter(), options);
        this.serializer = new PricingManagerSerializer();
    }

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("[ERROR] Expected 3 arguments but 2 were given");
            System.out.println("""
                    Yaml4SaaSUpdater
                    Description:
                    Utility that updates Yaml4SaaS pricings to the given version
                    of the specification
                    Usage:
                    <source_folder_path> <destination_folder_path> <version>

                    Supported version are:
                    1.1

                    Examples:

                    ./pricings ./pricing-updated 1.1

                    """);
            return;
        }

        if (args[0] == null) {
            System.out.println(
                    "Source folder is null. You have not specified the source directory to read configuration files");
            return;
        }

        if (args[1] == null) {
            System.out.println(
                    "Destination folder is null. You have not specified a directory to dump the configuration files");
            return;
        }

        if (args[2] == null) {
            System.out.println("Version is null. Current supported versions are " + Arrays.toString(Version.values()));
            return;
        }

        Version version = null;
        try {
            version = Version.version(args[2]);
        } catch (Exception e) {
            System.out
                    .println("[ERROR] <version>: Unrecognized version " + args[2] + ".\nSupported versions are:\n1.1");
            return;
        }

        File sourceDirectory = new File(args[0]);
        File destinationDirectory = new File(args[1]);

        if (!sourceDirectory.exists()) {
            System.out.printf(
                    "[ERROR] <source_folder_path>:\nSpecified path " + sourceDirectory.getPath() + " doesn't exist");
            return;
        }

        if (!destinationDirectory.exists()) {
            System.out.printf("[ERROR] <destination_folder_path>:\nSpecified path " + destinationDirectory.getPath()
                    + " doesn't exist");
            return;
        }

        if (sourceDirectory.isFile()) {
            System.out.println(
                    "[ERROR] <source_folder_path>: Provided path " + sourceDirectory.getPath() + " is not a directory");
            return;
        }

        if (destinationDirectory.isFile()) {
            System.out.println("[ERROR] <destination_folder_path>: Provided path " + destinationDirectory.getPath()
                    + " is not a directory");
            return;
        }

        System.out.println("Scanning directory " + sourceDirectory.getPath() + " ");

        VersionTransformer versionTransformer = new VersionTransformer();

        try {
            versionTransformer.processFiles(sourceDirectory, destinationDirectory, version);
            System.out.println();
            System.out.println("DONE!");
            System.out.println("Dumped files in directory " + destinationDirectory.getPath());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void processFiles(File src, File dst, Version targetVersion) {

        try (Stream<Path> stream = Files.walk(src.toPath(), FileVisitOption.FOLLOW_LINKS)) {
            stream.forEach((path) -> {
                File file = path.toFile();
                if (file.isFile() && this.hasYamlExtension(file.getName())) {
                    Map<String, Object> configFile = this.loadYaml4SaaSFile(file);
                    if (configFile != null) {
                        try {
                            YamlUpdater.update(configFile);
                            PricingManager updatedPricing = PricingManagerParser.parseMapToPricingManager(configFile);
                            this.writeUpdatedFile(updatedPricing, dst, file.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeUpdatedFile(PricingManager pricingManager, File destination, String fileName) {
        System.out.println("Updating file " + fileName);

        try (FileWriter fileWriter = new FileWriter(destination + "/" + fileName)) {
            yaml.dump(this.serializer.serialize(pricingManager), fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasYamlExtension(String name) {
        return name.endsWith(".yml") || name.endsWith(".yaml");
    }

    private Map<String, Object> loadYaml4SaaSFile(File file) {
        try (FileInputStream fileInput = new FileInputStream(file)) {
            Map<String, Object> configFile = this.yaml.load(fileInput);
            PricingManagerParser.parseMapToPricingManager(configFile);
            return configFile;

        } catch (PricingParsingException e) {
            System.out.println(String.format("file '%s' at path '%s' could not be parsed",
                    file.getName(), file.getAbsolutePath()));
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
