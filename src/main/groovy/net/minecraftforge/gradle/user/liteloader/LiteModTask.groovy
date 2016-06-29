package net.minecraftforge.gradle.user.liteloader

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.tasks.OutputFile
import org.gradle.api.internal.ClosureBackedAction

class LiteModTask extends DefaultTask
{

    @OutputFile File output = new File(this.temporaryDir, 'litemod.json')
    
    @Lazy LiteModJson json = {
            def version = project.extensions.minecraft.version
            new LiteModJson(project, version)
        } ()
    
    
    LiteModTask()
    {
        this.outputs.upToDateWhen { false }
        this.doFirst {
            if (json.revision == null) {
                project.ant.buildnumber()
                def buildNumber = project.ant.antProject.properties['build.number']
                json.revision = buildNumber
            }
        }

        this.doLast {
            output.delete()
            this.json.toJsonFile(output)
        }
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