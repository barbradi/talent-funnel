package co.uk.e4s.technical.test.mapper

interface GenericMapper<INPUT,OUTPUT> {
    open fun apply(var1: INPUT): OUTPUT
}