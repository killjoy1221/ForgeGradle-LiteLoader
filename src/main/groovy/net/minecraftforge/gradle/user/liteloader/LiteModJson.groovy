package net.minecraftforge.gradle.user.liteloader

import com.google.gson.*
import org.gradle.api.*

class LiteModJson
{
    static class Description extends HashMap<String, Object>
    {
        static final String BASE = ""

        private static final long serialVersionUID = 1L

        Description(base) {
            this[BASE] = base
        }

        @Override
        String toString()
        {
            this[Description.BASE] ?: Description.BASE
        }

    }
    
    String name, displayName, version, author
    String mcversion, revision
    Description description
    String injectAt, tweakClass
    List<String> classTransformerClasses
    List<String> dependsOn
    List<String> requiredAPIs
    List<String> mixinConfigs
    
    private transient final Project project
    private transient final String minecraftVersion
    
    LiteModJson(Project project, String minecraftVersion, String revision)
    {
        this.project = project
        this.mcversion = this.minecraftVersion = minecraftVersion
        this.revision = revision
        
        this.name = project.name
        this.description = new Description(project.description)
        this.displayName = project.hasProperty("displayName") ? project.property("displayName") : null
        this.version = project.version
    }

    def getClassTransformerClasses() {
        if (classTransformerClasses == null)
            classTransformerClasses = []
        classTransformerClasses
    }

    def getDependsOn() {
        if (dependsOn == null)
            dependsOn = []
        dependsOn
    }

    def getRequiredAPIs() {
        if (requiredAPIs == null)
            requiredAPIs = []
        requiredAPIs
    }

    def getMixinConfigs() {
        if (mixinConfigs == null)
            mixinConfigs = []
        mixinConfigs
    }

    def setDescription(value) {
        this.description[Description.BASE] = value
    }

    def toJsonFile(File outputFile) throws IOException
    {
        this.validate()
        outputFile.withWriter {
            new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Description, new JsonAdapter())
                .create()
                .toJson(this, it)
        }
    }

    private void validate()
    {
        def missing = []
        if (!this.name)
          missing += 'name'
        
        if (!this.version)
            missing += 'version'
        
        if (!this.mcversion)
            missing += 'mcverion'
        
        if (!this.revision)
            missing += 'revision'
        
        if (!missing.empty)
            throw new InvalidUserDataException("litemod json is missing properties $missing")
        
        try
        {
            Float.parseFloat(this.revision)
        }
        catch (NumberFormatException ex)
        {
            throw new InvalidUserDataException("invalid format for [revision] property in litemod.json, expected float")
        }
        
        if (this.minecraftVersion != this.mcversion) {
            this.project.logger.warn("You are setting a different target version of minecraft to the build environment")
        }

    }
}