package io.github.isagroup.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import io.github.isagroup.exceptions.CloneFeatureException;

/**
 * Object to model pricing features
 */
public abstract class Feature implements Serializable {
    protected String name;
    protected String description;
    protected ValueType valueType;
    protected Object defaultValue;
    protected transient Object value;
    protected String expression;
    protected String serverExpression;
    protected String tag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getServerExpression() {
        return serverExpression;
    }

    public void setServerExpression(String serverExpression) {
        this.serverExpression = serverExpression;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void prepareToPlanWriting() {
        this.name = null;
        this.value = null;
        this.description = null;
        this.defaultValue = null;
        this.expression = null;
        this.serverExpression = null;
        this.tag = null;
    }

    public boolean hasOverwrittenDefaultValue() {
        return !defaultValue.equals(value);
    }

    @Override
    public String toString() {
        return "Feature [name=" + name + ", description=" + description + ", valueType=" + valueType + ", defaultValue="
                + defaultValue + ", expression=" + expression + ", serverExpression=" + serverExpression + ", tag="
                + tag + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((valueType == null) ? 0 : valueType.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((expression == null) ? 0 : expression.hashCode());
        result = prime * result + ((serverExpression == null) ? 0 : serverExpression.hashCode());
        result = prime * result + ((tag == null) ? 0 : tag.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Feature other = (Feature) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (valueType != other.valueType)
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        if (expression == null) {
            if (other.expression != null)
                return false;
        } else if (!expression.equals(other.expression))
            return false;
        if (serverExpression == null) {
            if (other.serverExpression != null)
                return false;
        } else if (!serverExpression.equals(other.serverExpression))
            return false;
        if (tag == null) {
            if (other.tag != null)
                return false;
        } else if (!tag.equals(other.tag))
            return false;
        return true;
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

        if (tag != null) {
            attributes.put("tag", tag);
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
