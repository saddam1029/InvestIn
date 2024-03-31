object BottomNavigationManager {
//    fun setupBottomNavigation(context: Context, bottomNavigationView: BottomNavigationView) {
//        // Set Home selected
//        bottomNavigationView.selectedItemId = R.id.home
//
//        // Perform item selected listener
//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.message -> {
//                    navigateTo(context, Chat::class.java)
//                    true
//                }
//                R.id.home -> {
//                    navigateTo(context, Home::class.java)
//                    true
//                }
//                R.id.advice -> {
//                    navigateTo(context, Advice::class.java)
//                    true
//                }
//                R.id.notification -> {
//                    navigateTo(context, Notification::class.java)
//                    true
//                }
//                R.id.search -> {
//                    navigateTo(context, Search::class.java)
//                    true
//                }
//                else -> false
//            }
//        }
//    }
//
//    private fun navigateTo(context: Context, cls: Class<*>) {
//        val intent = Intent(context, cls)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//        startActivity(context, intent, null)
//    }
}
