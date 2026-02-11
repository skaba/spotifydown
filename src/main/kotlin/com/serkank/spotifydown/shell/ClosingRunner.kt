package com.serkank.spotifydown.shell

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.shell.core.ShellRunner
import org.springframework.stereotype.Component
import xyz.gianlu.librespot.core.Session

@Component
class ClosingRunner(
    private val shellRunner: ShellRunner,
    private val session: Session,
    @Value("\${close-session:true}") private val closeSession: Boolean,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        shellRunner.run(args.sourceArgs)
        if (closeSession) {
            session.close()
        }
    }
}
