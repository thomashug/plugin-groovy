package org.jboss.forge.plugin.groovy;

import static org.junit.Assert.assertTrue;

import org.apache.maven.model.Model;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

public class GroovyPluginTest extends AbstractShellTest
{
   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractShellTest.getDeployment().addPackages(true, GroovyPlugin.class.getPackage());
   }

   @Test
   public void should_setup() throws Exception
   {
      // given
      initializeJavaProject();
      queueInputLines("\n");
      
      // when
      getShell().execute("groovy setup");
      
      // then
      assertTrue(getProject().hasFacet(GroovyFacet.class));
      System.out.println(getOutput());
      String pomXml = Streams.toString(getProject().getFacet(MavenCoreFacet.class).getPOMFile().getResourceInputStream());
      System.out.println(pomXml);
      Model pom = getProject().getFacet(MavenCoreFacet.class).getPOM();
      pom.getBuild().getPlugins();
   }

}
