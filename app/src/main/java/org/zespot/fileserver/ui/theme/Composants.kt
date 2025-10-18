package org.zespot.fileserver.ui.theme

//import org.zespot.fileserver.getLangue
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.zespot.fileserver.R
import org.zespot.fileserver.addCustomLanguages
import org.zespot.fileserver.dictionaries
import org.zespot.fileserver.getLangue
import org.zespot.fileserver.repository.DataStoreManager


@Composable
fun Titre (modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val titre = context.getString(R.string.titre)

//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = modifier
//            .fillMaxWidth()
//            .wrapContentHeight()
//            //.height(120.dp)
//    ) {
//        Spacer(Modifier.height(10.dp))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.zespot48),
                contentDescription = "zespot",
                modifier = Modifier.size(height = 42.dp, width = 42.dp)
                //modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = titre,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 42.sp
            )
        }
    }
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavHostController,
    isDarkThemeActivated: Boolean,
    onDarkThemeToggle: (Boolean) -> Unit,
    debugMod: Boolean,
    debugModToggle: (Boolean) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    //val languages = listOf("English", "Français")
    val languages = dictionaries.getLanguageList()
    val context = LocalContext.current
    var selectedLanguage by rememberSaveable { mutableStateOf(languages[0]) }
    selectedLanguage = getLangue()
    addCustomLanguages()
    TopAppBar(
        title = { Titre() },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            Icon(
                imageVector = Icons.Default.Menu, contentDescription = null,
                modifier = Modifier.clickable {
                    expanded = !expanded
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

// Sélection de la langue
                languages.forEach { displayName ->
                    val code = displayName
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (code == selectedLanguage),
                                onClick = {
                                    selectedLanguage = code
                                    GlobalScope.launch {
                                        DataStoreManager.setLanguage(context, code)
                                    }
                                    dictionaries.initLanguage(code) // Mettre à jour la langue courante
                                }
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (code == selectedLanguage),
                            onClick = {
                                selectedLanguage = code
                                GlobalScope.launch {
                                    DataStoreManager.setLanguage(context, code)
                                }
                                dictionaries.initLanguage(code) // Mettre à jour la langue courante
                            }
                        )
                        Text(
                            text = displayName,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
// Fin de la langue

                DropdownMenuItem(
                    text = { Text("Dark mode") },
                    trailingIcon = {
                        Switch(
                            checked = isDarkThemeActivated,
                            onCheckedChange = {  onDarkThemeToggle(it)}
                        )
                    },
                    onClick = {
                        /* * To Do */
                    },
                )

                DropdownMenuItem(
                    text = { Text("Debug Mode") },
                    trailingIcon = {
                        Switch(
                            checked = debugMod,
                            onCheckedChange = {  debugModToggle(it)}
                        )
                    },
                    onClick = {
                        /* * To Do */
                    },
                )

            }
        }
    )
}

@Composable
fun Affiche_t1 ( titre : String) {
    Text(
        text = titre,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier=Modifier.border(2.dp, Color.Green).fillMaxWidth(). padding(10.dp),
        color = Color.Blue,
        textAlign = TextAlign.Center
    )
}

@Composable
fun Affiche_t2 ( titre : String) {
    Text(
        text = titre,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        modifier=Modifier.fillMaxWidth(). padding(10.dp),
        color = Color.Blue,
        textAlign = TextAlign.Left
    )
}