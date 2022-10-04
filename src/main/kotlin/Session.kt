import models.Context
import models.User

object Session {
    var isLoggedIn: Boolean = false
    var user: User? = null
    var context: Context = Context()
}