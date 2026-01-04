import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.pegapista.navigation.NavigationGraph

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PegaPistaScreen() {
    val navController = rememberNavController()
    NavigationGraph(
        navController = navController
    )
}