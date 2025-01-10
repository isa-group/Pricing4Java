package io.github.isagroup.services.yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import io.github.isagroup.services.updaters.YamlUpdater;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import io.github.isagroup.exceptions.FilepathException;
import io.github.isagroup.exceptions.SerializerException;
import io.github.isagroup.exceptions.UpdateException;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.parsing.PricingManagerParser;
import io.github.isagroup.services.serializer.PricingManagerSerializer;

/**
 * Utility class to handle YAML files
 */
public class YamlUtils {

    // Private constructor to hide the implicit public one
    private YamlUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final String DEFAULT_YAML_WRITE_MAIN_PATH = "src/main/resources/";
    private static final String DEFAULT_YAML_WRITE_TEST_PATH = "src/test/resources/";

    /**
     * This method maps the content of the YAML file located in {@code yamlPath}
     * into a {@link PricingManager} object.
     *
     * @param receivedYamlPath Path of the YAML file, relative to the resources
     *                         folder
     * @return PricingManager object that represents the content of the YAML file
     */

    public static PricingManager retrieveManagerFromYaml(String receivedYamlPath) {
        Yaml yaml = new Yaml();
        try {

            String yamlPath = getYamlPath(receivedYamlPath);

            String result = new String(Files.readAllBytes(Paths.get(yamlPath)));
            Map<String, Object> configFile = yaml.load(result);
            YamlUpdater.update(configFile);
            return PricingManagerParser.parseMapToPricingManager(configFile);

        } catch (IOException e) {
            throw new FilepathException("Either the file path is invalid or the file does not exist.");
        } catch (UpdateException e) {
            auxWriteYaml(e.getConfigFile());
        }
        return null;
    }

    /**
     * Writes a {@link PricingManager} object into a YAML file.
     *
     * @param pricingManager   a {@link PricingManager} object that represents a
     *                         pricing configuration
     * @param receivedYamlPath Path of the YAML file, relative to the resources
     *                         folder
     */
    public static void writeYaml(PricingManager pricingManager, String receivedYamlPath) {

        if (receivedYamlPath == null) {
            throw new FilepathException("Either the file path is invalid or the file does not exist.");
        }

        DumperOptions dump = new DumperOptions();
        dump.setIndent(2);
        dump.setPrettyFlow(true);
        dump.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Representer representer = new SkipNullRepresenter();

        PricingManagerSerializer pricingManagerSerializer = new PricingManagerSerializer();
        try {

            String yamlPath = null;

            try {
                yamlPath = getYamlPath(receivedYamlPath);
            } catch (IOException e) {
                yamlPath = DEFAULT_YAML_WRITE_MAIN_PATH + receivedYamlPath;
            }

            FileWriter writer = new FileWriter(yamlPath);

            Map<String, Object> serializedPricingManager = pricingManagerSerializer.serialize(pricingManager);
            Yaml yaml = new Yaml(representer, dump);
            yaml.dump(serializedPricingManager, writer);
            writer.close();

        } catch (IOException e) {
            throw new FilepathException("Either the file path is invalid or the file does not exist.");
        } catch (SerializerException e) {
            throw new SerializerException("An error occurred while serializing the PricingManager object.");
        }
    }

    private static void auxWriteYaml(Map<String, Object> configFile) {

        DumperOptions dump = new DumperOptions();
        dump.setIndent(2);
        dump.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Representer representer = new SkipNullRepresenter();

        try {
            FileWriter writer = new FileWriter(DEFAULT_YAML_WRITE_MAIN_PATH + "yaml-testing/errored.yml");

            Yaml yaml = new Yaml(representer, dump);
            yaml.dump(configFile, writer);
            writer.close();
        } catch (IOException e) {
            throw new FilepathException("Either the file path is invalid or the file does not exist.");
        }
    }

    private static String getYamlPath(String receivedPath) throws IOException {
        if (!Files.exists(Paths.get(DEFAULT_YAML_WRITE_MAIN_PATH + receivedPath))) {
            if (!Files.exists(Paths.get(DEFAULT_YAML_WRITE_TEST_PATH + receivedPath))) {
                throw new IOException("Either the file path is invalid or the file does not exist.");
            }
            return DEFAULT_YAML_WRITE_TEST_PATH + receivedPath;
        } else {
            return DEFAULT_YAML_WRITE_MAIN_PATH + receivedPath;
        }
    }
}
