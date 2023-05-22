package me.panxin.plugin.idea.jumpcontroller.enumclass;

/**
 * @author PanXin
 * @version $ Id: SpringRequestMethodAnnotation, v 0.1 2023/05/16 11:25 PanXin Exp $
 */
public enum SpringControllerClassAnnotation {
    /**
     * RequestMapping
     */
    CONTROLLER("org.springframework.stereotype.Controller"),
    /**
     * GetMapping
     */
    RESTCONTROLLER("org.springframework.web.bind.annotation.RestController");

    private final String qualifiedName;

    SpringControllerClassAnnotation(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }


    public static SpringControllerClassAnnotation getByShortName(String requestMapping) {
        for (SpringControllerClassAnnotation springRequestAnnotation : SpringControllerClassAnnotation.values()) {
            if (springRequestAnnotation.getQualifiedName().endsWith(requestMapping)) {
                return springRequestAnnotation;
            }
        }
        return null;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }
}