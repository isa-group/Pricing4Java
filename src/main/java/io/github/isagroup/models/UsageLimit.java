package io.github.isagroup.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.isagroup.exceptions.CloneUsageLimitException;

public abstract class UsageLimit implements Serializable {
    private String name;
    private String description;
    private ValueType valueType;
    private Object defaultValue;
    protected UsageLimitType type;
    private String unit;
    private transient Object value;
    private List<String> linkedFeatures = new ArrayList<>();
    private String expression;
    private String serverExpression;

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

    public UsageLimitType getType() {
        return type;
    }

    public void setType(UsageLimitType type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<String> getLinkedFeatures() {
        return linkedFeatures;
    }

    public void setLinkedFeatures(List<String> linkedFeatures) {
        this.linkedFeatures = linkedFeatures;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((valueType == null) ? 0 : valueType.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((linkedFeatures == null) ? 0 : linkedFeatures.hashCode());
        result = prime * result + ((expression == null) ? 0 : expression.hashCode());
        result = prime * result + ((serverExpression == null) ? 0 : serverExpression.hashCode());
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
        UsageLimit other = (UsageLimit) obj;
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
        if (type != other.type)
            return false;
        if (unit == null) {
            if (other.unit != null)
                return false;
        } else if (!unit.equals(other.unit))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        if (linkedFeatures == null) {
            if (other.linkedFeatures != null)
                return false;
        } else if (!linkedFeatures.equals(other.linkedFeatures))
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
        return true;
    }

    @Override
    public String toString() {
        return "UsageLimit [name=" + name + ", description=" + description + ", valueType=" + valueType
                + ", defaultValue=" + defaultValue + ", type=" + type + ", unit=" + unit + ", linkedFeatures="
                + linkedFeatures + ", expression=" + expression + ", serverExpression=" + serverExpression + "]";
    }

    /*
     * This method returns a boolean indicating wether the feature whose name is received as a parameter is linked to this usage limit.
     * @param featureName The name of the feature to check if it is linked to this usage limit.
     * @return A boolean indicating wether the feature whose name is received as a parameter is linked to this usage limit.
     */
    public boolean isLinkedToFeature(String featureName) {
        return linkedFeatures.contains(featureName);
    }

    public Map<String, Object> serialize() {
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

        if (unit != null) {
            attributes.put("unit", unit);
        }

        attributes.put("type", type.toString());

        if (linkedFeatures != null && !linkedFeatures.isEmpty()) {
            attributes.put("linkedFeatures", linkedFeatures);
        }

        if (expression != null) {
            attributes.put("expression", expression);
        }

        if (serverExpression != null) {
            attributes.put("serverExpression", serverExpression);
        }

        return attributes;
    }

    public static UsageLimit cloneUsageLimit(UsageLimit original) throws CloneUsageLimitException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(original);

            // Deserializa el objeto desde el flujo de bytes, creando una copia
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            return (UsageLimit) objectInputStream.readObject();

        } catch (Exception e) {
            throw new CloneUsageLimitException("Error cloning usageLimit");
        }
    }

}
