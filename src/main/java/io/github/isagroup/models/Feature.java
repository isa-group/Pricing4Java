package io.github.isagroup.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import io.github.isagroup.exceptions.CloneFeatureException;
import lombok.Getter;
import lombok.Setter;

/**
 * Object to model pricing features
 */
@Getter
@Setter
public abstract class Feature implements Serializable {
    protected String name;
    protected String description;
    protected ValueType valueType;
    protected Object defaultValue;
    protected transient Object value;
    protected String expression;
    protected String serverExpression;

    public void prepareToPlanWriting() {
        this.name = null;
        this.value = null;
        this.description = null;
        this.defaultValue = null;
        this.expression = null;
        this.serverExpression = null;
    }

    public Map<String, Object> featureAttributesMap() {
        Map<String, Object> attributes = new LinkedHashMap<>();

        if (description != null) {
            attributes.put("description", description);
        }

        if (valueType != null) {
            attributes.put("valueType", valueType.toString());
        }

        if (defaultValue != null) {
            attributes.put("defaultValue", defaultValue);
        }

        if (expression != null) {
            attributes.put("expression", expression);
        }

        if (serverExpression != null) {
            attributes.put("serverExpression", serverExpression);
        }
        return attributes;
    }

    public abstract Map<String, Object> serializeFeature();

    public static Feature cloneFeature(Feature original) throws CloneFeatureException {
        try {
            // Serializa el objeto original en un flujo de bytes
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(original);

            // Deserializa el objeto desde el flujo de bytes, creando una copia
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            return (Feature) objectInputStream.readObject();
        } catch (Exception e) {
            throw new CloneFeatureException("Error cloning feature");
        }
    }
}
