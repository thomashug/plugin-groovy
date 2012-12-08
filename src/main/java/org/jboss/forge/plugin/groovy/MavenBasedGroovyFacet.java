package org.jboss.forge.plugin.groovy;

import java.util.List;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.dependencies.DependencyQueryBuilder;
import org.jboss.forge.project.dependencies.NonSnapshotDependencyFilter;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet(MavenCoreFacet.class)
public class MavenBasedGroovyFacet extends BaseFacet implements GroovyFacet
{
   
   private static final DependencyBuilder GROOVY = DependencyBuilder.create("org.codehaus.groovy:groovy-all");
   private static final DependencyBuilder GROOVY_COMPILER = DependencyBuilder.create("org.codehaus.groovy:groovy-eclipse-compiler");
/*
 * <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
          <compilerId>groovy-eclipse-compiler</compilerId>
          <verbose>false</verbose>
        </configuration>
        <dependencies>
          <dependency>
            <groupId>org.codehaus.groovy</groupId>
            <artifactId>groovy-eclipse-compiler</artifactId>
            <version>${version.groovy.eclipse_compiler}</version>
            <exclusions>
              <exclusion>
                <groupId>org.codehaus.groovy</groupId>
                <artifactId>groovy-all</artifactId>
              </exclusion>
            </exclusions>
          </dependency>
 */
   @Inject
   private DependencyInstaller installer;
   
   @Inject
   private ShellPrompt prompt;

   @Override
   public boolean install()
   {
      DependencyBuilder groovy = DependencyBuilder.create(installDependency(GROOVY));
      project.getProjectRoot().getChildDirectory("src/main/groovy").mkdir();
      DependencyBuilder groovyCompiler = DependencyBuilder.create(promptVersion(GROOVY_COMPILER));
      groovyCompiler.addExclusion()
            .setArtifactId(groovy.getArtifactId())
            .setGroupId(groovy.getGroupId());
      Model pom = project.getFacet(MavenCoreFacet.class).getPOM();
      MavenPluginBuilder plugin = MavenPluginBuilder.create()
               .setDependency(DependencyBuilder.create("org.apache.maven.plugins:maven-compiler-plugin"))
               .addPluginDependency(groovyCompiler);
      ConfigurationBuilder configBuilder = plugin.createConfiguration();
      configBuilder.createConfigurationElement("compilerId").setText(groovyCompiler.getArtifactId());
      configBuilder.createConfigurationElement("verbose").setText("false");
      plugin.getConfig();
      pom.getBuild().addPlugin(new MavenPluginAdapter(plugin));
      
      getProject().getFacet(MavenCoreFacet.class).setPOM(pom);
      return true;
   }

   private Dependency installDependency(Dependency dependency)
   {
      Dependency chosen = promptVersion(dependency);
      return installer.install(project, chosen);
   }

   @Override
   public boolean isInstalled()
   {
      List<Dependency> dependencies = project.getFacet(DependencyFacet.class).getDependencies();
      for (Dependency dependency : dependencies)
      {
         if (GROOVY.getArtifactId().equals(dependency.getArtifactId()) &&
                  GROOVY.getGroupId().equals(dependency.getGroupId())) {
            return true;
         }
      }
      return false;
   }
   
   private Dependency promptVersion(Dependency dependency)
   {
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      Dependency result = dependency;
      List<Dependency> versions = dependencyFacet.resolveAvailableVersions(DependencyQueryBuilder.create(dependency)
               .setFilter(new NonSnapshotDependencyFilter()));
      if (versions.size() > 0)
      {
         Dependency deflt = versions.get(versions.size() - 1);
         result = prompt.promptChoiceTyped("Use which version of '" + dependency.getArtifactId()
                  + "' ?", versions, deflt);
      }
      return result;
   }

}
