package net.minecraftforge.gradle.user.liteloader

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.internal.ClosureBackedAction

import java.util.Properties

class LiteModTask extends DefaultTask
{

    @OutputFile File output = new File(this.temporaryDir, this.fileName)
    
    @Lazy String buildNumber = {
            project.ant.buildnumber()
            project.ant.antProject.properties['build.number']
        } ()
    
    @Lazy LiteModJson json = {
            def version = project.extensions.minecraft.version
            new LiteModJson(project, version, buildNumber)
        } ()
    
    
    LiteModTask()
    {
        this.outputs.upToDateWhen { false }
    }

    @TaskAction
    void doTask() throws IOException
    {
        output.delete()
        this.json.toJsonFile(output)
    }
    
    def json(Closure configureClosure)
    {
        json(new ClosureBackedAction(configureClosure))
    }

    def json(Action<LiteModJson> action)
    {
        action.execute(this.json)
    }
}