package unam.mx.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseManager {

    companion object {

        private var auth: FirebaseAuth? = null
        private var db: FirebaseFirestore? = null

        fun getAuth(): FirebaseAuth {

            if (auth == null) {
                auth = FirebaseAuth.getInstance()
            }

            return auth!!
        }

        fun getDb(): FirebaseFirestore {

            if (db == null) {
                db = FirebaseFirestore.getInstance()
            }

            return db!!
        }
    }
}