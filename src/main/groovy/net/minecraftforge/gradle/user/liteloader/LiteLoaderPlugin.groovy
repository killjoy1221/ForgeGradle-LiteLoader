package net.minecraftforge.gradle.user.liteloader

import static net.minecraftforge.gradle.common.Constants.*
import static net.minecraftforge.gradle.user.UserConstants.*

import net.minecraftforge.gradle.user.UserVanillaBasePlugin;
import net.minecraftforge.gradle.util.json.JsonFactory
import net.minecraftforge.gradle.util.json.LiteLoaderJson
import net.minecraftforge.gradle.util.json.LiteLoaderJson.Artifact
import net.minecraftforge.gradle.util.json.LiteLoaderJson.RepoObject

import org.gradle.api.*

public class LiteloaderPlugin extends UserVanillaBasePlugin<LiteloaderExtension>
{
    static final String CONFIG_LL_DEOBF_COMPILE = 'liteloaderDeobfCompile'
    static final String CONFIG_LL_DC_RESOLVED = 'liteloaderResolvedDeobfCompile'

    static final String MAVEN_REPO_NAME = 'liteloaderRepo'

    static final String MODFILE_PREFIX = 'mod-'
    static final String MODFILE_EXTENSION = 'litemod'
    
    static final String VERSION_JSON_URL = 'http://dl.liteloader.com/versions/versions.json'
    static final String VERSION_JSON_FILE = "$REPLACE_CACHE_DIR/com/mumfrey/liteloader/versions.json"

    static final String TASK_LITEMOD = 'litemod'
    
    static final String MFATT_MODTYPE = 'ModType'
    static final String MODSYSTEM = 'LiteLoader'

    private LiteLoaderJson json

    RepoObject repo

    Artifact artifact

    @Override
    protected void applyVanillaUserPlugin()
    {
        def configs = this.project.configurations
        configs.maybeCreate(CONFIG_LL_DEOBF_COMPILE)
        configs.maybeCreate(CONFIG_LL_DC_RESOLVED)

        configs[CONFIG_DC_RESOLVED].extendsFrom configs[CONFIG_LL_DC_RESOLVED]
        
        def versionJson = delayedFile(VERSION_JSON_FILE)()
        def versionJsonEtag = delayedFile(VERSION_JSON_FILE + '.etag')()
        json = JsonFactory.loadLiteLoaderJson(getWithEtag(VERSION_JSON_URL, versionJson, versionJsonEtag))

        String baseName = MODFILE_PREFIX + this.project.archivesBaseName.toString().toLowerCase()

        def tasks = this.project.tasks
        def jar = tasks.jar
        jar.extension = MODFILE_EXTENSION
        jar.baseName = baseName
        
        def sourceJar = tasks.sourceJar
        sourceJar.baseName = baseName
        
        makeTask(TASK_LITEMOD, LiteModTask)
    }

    @Override
    protected void afterEvaluate()
    {
        super.afterEvaluate()
        this.applyJson()

        // If user has changed extension back to .jar, write the ModType
        // manifest attribute
        def jar = this.project.tasks.jar
        if ('jar' == jar.extension) {
            def attributes = jar.manifest.attributes
            if (MFATT_MODTYPE in attributes) {
                attributes[MFATT_MODTYPE] = MODSYSTEM
            }
        }
    }
    
    @Override
    protected void setupDevTimeDeobf(final Task compileDummy, final Task providedDummy)
    {
        super.setupDevTimeDeobf(compileDummy, providedDummy)
        
        // die with error if I find invalid types...
        this.project.afterEvaluate {
            if (project.state.failure != null)
                return
            
            remapDeps(project, project.configurations.getByName(CONFIG_LL_DEOBF_COMPILE), CONFIG_LL_DC_RESOLVED, compileDummy)
        }
    }
    
    private void applyJson()
    {
        if (this.json == null)
        {
            return
        }
        
        def version = this.json.versions.get(this.extension.version)
        if (version != null)
        {
            this.repo = version.repo
            this.artifact = version.latest
            this.applyDependenciesFromJson()
        }
    }
    
    private void applyDependenciesFromJson()
    {
        def repo = this.repo
        if (repo == null)
        {
            return
        }

        this.project.allprojects {
            addMavenRepo(it, MAVEN_REPO_NAME, repo.url)
        }

        def artifact = this.artifact
        if (artifact == null)
        {
            return
        }
        addDependency(project, CONFIG_LL_DEOBF_COMPILE, artifact.getDepString(repo))
        
        artifact.libraries.each { library ->
            def name = library.name
            if (name)
            {
                addDependency(project, CONFIG_MC_DEPS, name)
            }
            
            def url = library.url
            if (url)
            {
                addMavenRepo(project, url, url)
            }
        }
    }

    def getVersion(String version)
    {
        this.json?.versions?.get(version)
    }

    @Override
    protected String getJarName()
    {
        'minecraft'
    }

    @Override
    protected void createDecompTasks(String globalPattern, String localPattern)
    {
        super.makeDecompTasks(globalPattern, localPattern, delayedFile(JAR_CLIENT_FRESH), TASK_DL_CLIENT, delayedFile(MCP_PATCHES_CLIENT), delayedFile(MCP_INJECT));
    }

    @Override
    protected boolean hasServerRun()
    {
        false
    }

    @Override
    protected boolean hasClientRun()
    {
        true
    }

    @Override
    protected Object getStartDir()
    {
        delayedFile("$REPLACE_CACHE_DIR/net/minecraft/$jarName/$REPLACE_MC_VERSION/start")
    }

    @Override
    protected String getClientTweaker(LiteloaderExtension ext)
    {
        'com.mumfrey.liteloader.launch.LiteLoaderTweaker'
    }

    @Override
    protected String getClientRunClass(LiteloaderExtension ext)
    {
        'com.mumfrey.liteloader.debug.Start'
    }

    @Override
    protected String getServerTweaker(LiteloaderExtension ext)
    {
        ''// never run on server.. so...
    }

    @Override
    protected String getServerRunClass(LiteloaderExtension ext)
    {
        // irrelevant..
        ''
    }

    @Override
    protected List<String> getClientJvmArgs(LiteloaderExtension ext)
    {
        ext.resolvedClientJvmArgs
    }

    @Override
    protected List<String> getServerJvmArgs(LiteloaderExtension ext)
    {
        ext.resolvedServerJvmArgs
    }

    protected void addDependency(Project proj, String configuration, String dependency)
    {
        proj.dependencies.add(configuration, dependency)
    }
}