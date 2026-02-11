package com.serkank.spotifydown.shell

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.shell.core.ShellRunner
import org.springframework.stereotype.Component

@Component
class ClosingRunner(
    private val shellRunner: ShellRunner,
    private val applicationContext: ConfigurableApplicationContext,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        shellRunner.run(args.sourceArgs)
        applicationContext.close()
    }
}
