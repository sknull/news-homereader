package de.visualdigits.newshomereader.model.clientdata

class ClientData(
    var hideRead: Boolean = false,
    var readItems: MutableSet<UInt> = mutableSetOf()
)