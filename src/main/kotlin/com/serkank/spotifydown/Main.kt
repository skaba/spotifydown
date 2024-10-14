package com.serkank.spotifydown

import javazoom.spi.mpeg.sampled.file.MpegAudioFormat
import vavi.sound.sampled.mp3.MpegAudioFileWriter
import java.io.File
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

class Main

fun main(args: Array<String>) {
    val mp3foo = AudioSystem.getAudioInputStream(File("/home/serkan/Music/djndrums/yerli/19 - Tarkan - Kimdi.mp3"))
    println(mp3foo.format)
    val ogg = File("/home/serkan/workspace/spotifydown/Nirvana - Smells Like Teen Spirit.ogg")
    val mp3 = File("/home/serkan/workspace/spotifydown/Nirvana - Smells Like Teen Spirit.mp3")
    val wav = File("/home/serkan/workspace/mp3spi/src/test/resources/test.wav")
    val wav2 = File("/home/serkan/workspace/spotifydown/Nirvana - Smells Like Teen Spirit.wav")
    // val format = AudioSystem.getAudioInputStream(wav).format
    val oggIn = AudioSystem.getAudioInputStream(ogg)
    println(oggIn.format)

    val targetAudioFormat =
        AudioFormat(44100f, 16, 2, true, false)
    // val mp3AudioFormat = AudioFormat
    // oggIn.reset()
    // println(AudioSystem.isConversionSupported(format, oggIn.format))
    // oggIn.reset()
    // val decodedStream = AudioSystem.getAudioInputStream(targetAudioFormat, oggIn)
    // clip.open(decodedStream)

    // clip.wait()
    // oggIn.reset()
    // val mp3AudioFormat = MP3A
    /*val mp3AudioFormat =
        AudioFormat(
            Encodings.getEncoding("MP3");
            AudioSystem.NOT_SPECIFIED.toFloat(),
            AudioSystem.NOT_SPECIFIED,
            2,
            AudioSystem.NOT_SPECIFIED,
            AudioSystem.NOT_SPECIFIED.toFloat(),
            true
        )*/
    val mp3AudioFormat =
        MpegAudioFormat(
            MpegAudioFileWriter.MPEG1L3,
            oggIn.format.sampleRate,
            oggIn.format.sampleSizeInBits,
            2,
            oggIn.format.frameSize,
            oggIn.format.frameRate,
            true,
            mapOf("bitrate" to "320"),
        )
    val pcmInputStream = AudioSystem.getAudioInputStream(targetAudioFormat, oggIn)
    val mp3InputStream = AudioSystem.getAudioInputStream(mp3AudioFormat, pcmInputStream)

    // println(AudioSystem.getAudioFileTypes())

    // mp3InputStream.reset()
    // val out = wav2.outputStream()
    // IOUtils.copy(pcmInputStream, out)
    // out.flush()
    // out.close()

    // val mp3InputStream =
    AudioSystem.write(mp3InputStream, MpegAudioFileWriter.MP3, mp3)

    /*val decodedFormat =
        AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            oggIn.format.sampleRate,
            16,
            oggIn.format.channels,
            oggIn.format.channels * 2,
            oggIn.format.sampleRate,
            false,
        )
    val decodedInputStream = AudioSystem.getAudioInputStream(decodedFormat, oggIn)
    val out = wav.outputStream()
    IOUtils.copy(decodedInputStream, out)
    out.flush()
     */

    // oggIn.reset()

    /*val inputFormat = ain.format

    val pcmFormat =
        AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            inputFormat.sampleRate,
            16,
            inputFormat.channels,
            inputFormat.channels * 2,
            inputFormat.sampleRate,
            false,
        )
    val mp3AudioFormat =
        AudioFormat(
            AudioFormat.Encoding("MPEG1L3"),
            inputFormat.sampleRate,
            -1,
            inputFormat.channels,
            -1,
            0F,
            true,
        )
    val pcmInputStream = AudioSystem.getAudioInputStream(pcmFormat, ain)
    val mp3AudioStream = AudioSystem.getAudioInputStream(mp3AudioFormat, pcmInputStream)
     */

    // MPEG1L3
}
