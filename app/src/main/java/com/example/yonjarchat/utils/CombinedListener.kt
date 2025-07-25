package com.example.yonjarchat.utils

import com.google.firebase.firestore.ListenerRegistration

class CombinedListener(
    private val first: ListenerRegistration,
    private val second: ListenerRegistration
) : ListenerRegistration {
    override fun remove() {
        first.remove()
        second.remove()
    }
}

class DummyListenerRegistration : ListenerRegistration {
    override fun remove() {}
}
