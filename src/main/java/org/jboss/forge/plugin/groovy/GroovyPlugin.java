package org.jboss.forge.plugin.groovy;

import java.util.Arrays;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeIn;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;

/**
 *
 */
@Alias("groovy")
@RequiresFacet(GroovyFacet.class)
public class GroovyPlugin implements Plugin
{
   @Inject
   private ShellPrompt prompt;
   
   @Inject
   private Event<InstallFacets> install;

   @SetupCommand
   public void setup(@PipeIn String in, PipeOut out)
   {
      install.fire(new InstallFacets(GroovyFacet.class));
   }

   @Command
   public void command(@PipeIn String in, PipeOut out, @Option String... args)
   {
      if (args == null)
         out.println("Executed named command without args.");
      else
         out.println("Executed named command with args: " + Arrays.asList(args));
   }

   @Command
   public void prompt(@PipeIn String in, PipeOut out)
   {
      if (prompt.promptBoolean("Do you like writing Forge plugins?"))
         out.println("I am happy.");
      else
         out.println("I am sad.");
   }
}
