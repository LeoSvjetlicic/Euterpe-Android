package ls.diplomski.euterpe.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ls.diplomski.euterpe.ui.camerascreen.CameraScreen
import ls.diplomski.euterpe.ui.detailsscreen.DetailsScreen
import ls.diplomski.euterpe.ui.musicsnippetlist.MusicSnippetListScreen
import ls.diplomski.euterpe.ui.theme.EuterpeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EuterpeTheme {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val showFAB by remember {
                    derivedStateOf {
                        navBackStackEntry?.destination?.route == MUSIC_SNIPPET_LIST_SCREEN_ROUTE
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        if (showFAB) {
                            FloatingActionButton(
                                onClick = {
                                    navController.navigate(CAMERA_SCREEN_ROUTE)
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            }
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = MUSIC_SNIPPET_LIST_SCREEN_ROUTE
                    ) {
                        composable(route = MUSIC_SNIPPET_LIST_SCREEN_ROUTE) {
                            MusicSnippetListScreen {
                                val encodedPath = Uri.encode(it)
                                navController.navigate("$DETAILS_SCREEN_BASE_ROUTE/$encodedPath")
                            }
                        }
                        composable(route = CAMERA_SCREEN_ROUTE) {
                            CameraScreen(
                                navController = navController
                            )
                        }
                        composable(
                            route = DETAILS_SCREEN_ROUTE,
                            arguments = listOf(navArgument(SNIPPET_PATH_KEY) {
                                type = NavType.StringType
                            }),
                        ) { backStackEntry ->
                            val midiPath = backStackEntry.arguments?.getString(SNIPPET_PATH_KEY)
                            val filePath = Uri.decode(midiPath ?: "")
                            filePath?.let {
                                DetailsScreen(
                                    midiFilePath = it,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
