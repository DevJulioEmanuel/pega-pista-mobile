package com.example.pegapista.ui.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pegapista.R // Certifique-se de ter um icone do google ou remova a Image
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun BotaoGoogle(
    onGoogleSignInSelected: (String) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current

    val WEB_CLIENT_ID = "800822141654-gvr2uinlf53fq85lb6s012qa1trvcl0t.apps.googleusercontent.com"

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { token ->
                    onGoogleSignInSelected(token)
                } ?: onError("Token Google nulo")
            } catch (e: ApiException) {
                Log.e("GoogleLogin", "Google sign in failed", e)
                onError("Falha Google: ${e.statusCode}")
            }
        }
    }

    Button(
        onClick = {
            launcher.launch(googleSignInClient.signInIntent)
        },
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp), clip = false)
            .height(50.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Logo Google",
            modifier = Modifier.size(24.dp)
         )
        Spacer(modifier = Modifier.width(8.dp))

        Text("Entrar com Google", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}