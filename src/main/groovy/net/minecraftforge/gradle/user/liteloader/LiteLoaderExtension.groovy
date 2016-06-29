package net.minecraftforge.gradle.user.liteloader

import com.google.common.base.Strings
import net.minecraftforge.gradle.user.UserBaseExtension
import org.gradle.api.InvalidUserDataException
import org.gradle.jvm.tasks.Jar

class LiteloaderExtension extends UserBaseExtension
{
    private final LiteloaderPlugin plugin
    
    LiteloaderExtension(LiteloaderPlugin plugin)
    {
        super(plugin)
        this.plugin = plugin
    }
    
    @Override
    void setVersion(String version)
    {
        super.setVersion(version)
        this.checkVersion(version)
        
        project.tasks.jar.with {
            classifier = classifier ?: "mc$version"
        }
    }

    private void checkVersion(String version)
    {
        if (this.plugin.getVersion(version) == null)
        {
            throw new InvalidUserDataException("No ForgeGradle-compatible LiteLoader version found for Minecraft  $version")
        }
    }
}