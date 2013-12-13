package org.craftercms.profile.api;

/**
 * Represents the definition of an attribute in a tenant.
 *
 * @author avasquez
 */
public class AttributeDefinition {

    private String name;
    private String label;
    private int order;
    private String type;
    private String constraint;
    private boolean required;

    /**
     * Returns the name of the attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the attribute name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the label that's displayed for the attribute on applications like Admin Consoles.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label that's displayed for the attribute on applications like Admin Consoles.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the display order of the attribute on applications like Admin Console.
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the display order of the attribute on applications like Admin Console.
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Returns the type of the attribute to use for convert (string, int, boolean, etc.).
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the attribute to use for convert (string, int, boolean, etc.).
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the constraint used to validate the field (regex).
     */
    public String getConstraint() {
        return constraint;
    }

    /**
     * Sets the constraint used to validate the field (regex).
     */
    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    /**
     * Returns true if the attribute is required, false otherwise.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets if the attribute is required or not.
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

}
