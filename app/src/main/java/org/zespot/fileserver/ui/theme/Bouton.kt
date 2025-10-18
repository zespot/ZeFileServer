package org.zespot.fileserver.ui.theme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.zespot.fileserver.R

val Vert = Color(0xFF009d00)
val Rouge = Color(0xFFff0000)
val Bleu = Color(0xFF0000FF)
val Jaune = Color(0xFFFFCE00)
val Gris = Color(0xFF808080)
val GrisClair = Color(0xFFA0A0A0)
val Noir = Color(0xFF000000)
val Blanc = Color(0xFFFFFFFF)




@Composable
fun BoutonVertRouge(
    modifier: Modifier = Modifier ,
    text: String,
    onClick: () -> Unit) {
    Bouton(modifier, text,Color.White,Color.Red ,  onClick)

}


@Composable
fun Bouton(
    modifier: Modifier = Modifier ,
    text: String,
    textcolor: Color,
    fondcolor: Color,
    onClick: () -> Unit) {
    Button(
        modifier = modifier
            .padding(0.dp),
        onClick = onClick,
        border = BorderStroke(2.dp, Vert),
        colors = ButtonDefaults.buttonColors(
            containerColor =fondcolor, // Couleur de fond prédéfinie
            contentColor = textcolor // Couleur du texte prédéfinie
        ),
    ) {
        Text(text)
    }
}

@Composable
fun BoutonIcone(
    modifier: Modifier = Modifier ,
    text: String,
    icon: ImageVector?=null,
    image:Painter?=null,
    descIcon: String,
    colorIcon: Color,
    colorBorder: Color,
    height: Int,
    onClick: () -> Unit) {
    val textLineHeight = MaterialTheme.typography.bodyMedium.lineHeight
    val iconSize: Dp = with(LocalDensity.current) { textLineHeight.toDp() } *height

    Button(
        modifier = modifier
            .padding(0.dp),
        onClick = onClick,
        border = BorderStroke(4.dp, colorBorder),
        colors = ButtonDefaults.buttonColors(
            containerColor =Color.White, // Couleur de fond prédéfinie
            contentColor = Color.Black // Couleur du texte prédéfinie
        ),
    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = descIcon,
                    tint = colorIcon,
                    modifier = Modifier.size(iconSize).padding(0.dp)
                )
            } else if (image != null) {
                Image(
                    painter = image,
                    contentDescription = "Image",
                    modifier = Modifier.size(iconSize).padding(0.dp)
                )
            }

            Text(text=text,
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            )
        }
  //  }
}

@Composable
fun BoutonStart(
    modifier: Modifier = Modifier ,
    text: String,
    onClick: () -> Unit) {
    BoutonIcone(modifier, text, Icons.Default.PlayArrow,null,"Start", Vert, Vert, 2 , onClick)
}

@Composable
fun BoutonIsStarted(
    modifier: Modifier = Modifier ,
    text: String,
    onClick: () -> Unit) {
    val stop : ImageVector =ImageVector.vectorResource(id=R.drawable.stop_circle_48px)
    BoutonIcone(modifier, text, stop,null,"Start", Gris, Vert, 2 , onClick)
}

@Composable
fun BoutonContact(
    modifier: Modifier = Modifier ,
    text: String,
    onClick: () -> Unit) {
    val Contact : ImageVector =ImageVector.vectorResource(id=R.drawable.contact_page_48px)
    BoutonIcone(modifier, text, Contact,null,"Start", GrisClair, GrisClair, 2 , onClick)
}

@Composable
fun BoutonLaunch(
    modifier: Modifier = Modifier ,
    text: String,
    onClick: () -> Unit) {
    //val open =    painterResource    (R.drawable.open_in_browser)
    val open : ImageVector =ImageVector.vectorResource(id=R.drawable.open_in_browser_48px)
    BoutonIcone(modifier, text,  open,null,"Launch", Vert, Vert, 2 , onClick)
}

@Composable
fun BoutonStatus(
    modifier: Modifier = Modifier ,
    text: String,
    onClick: () -> Unit) {
    BoutonIcone(modifier, text, Icons.Default.Info,null,"Info", Vert, Vert, 2 , onClick)
}

@Composable
fun BoutonRep(
    modifier: Modifier = Modifier ,
    text: String,
    onClick: () -> Unit) {
    BoutonIcone(modifier, text, Icons.Default.Settings,null,"Répertoire", GrisClair, GrisClair, 2 , onClick)
}

@Composable
fun BoutonStop(
    modifier: Modifier = Modifier ,
    text: String,
    onClick: () -> Unit) {
    BoutonIcone(modifier, text, Icons.Default.Close,null ,"Exit", Rouge, Vert, 2 , onClick)
}

@Composable
fun BoutonDebug(
    modifier: Modifier = Modifier ,
    text: String,
    onClick: () -> Unit) {
    val report : ImageVector =ImageVector.vectorResource(id=R.drawable.report_48px)
    BoutonIcone(modifier, text, report, null,"Debug",GrisClair, GrisClair, 2 , onClick)
}

@Composable
fun BoutonAide(modifier: Modifier, text:String,  onClick: () -> Unit) {
    OutlinedButton(    modifier = modifier.padding(0.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White, // Fond blanc
            contentColor = Color.Black // Texte noir pour le contraste
        ),
        onClick = onClick)
    {
        Text(text)
    }
}

@Composable
fun ligne () {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        HorizontalDivider(
            modifier = Modifier
                .width(200.dp), // Moitié de la largeur ou une valeur fixe selon vos besoins
            thickness = 4.dp,
            color = GrisClair
        )
    }
}


