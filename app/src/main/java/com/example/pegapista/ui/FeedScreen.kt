package com.example.pegapista.ui

import android.widget.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pegapista.data.Postagem
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.R

@Composable
fun FeedScreen(modifier: Modifier = Modifier.background(Color.White)) {
    val postagens = listOf(
        Postagem("Daniel Jacó", "5.2 km", "50:15 min"),
        Postagem("Henrique Mendes", "3.0 km", "30:00 min"),
        Postagem("Henrique Mendes", "3.0 km", "30:00 min")
    )
    Column() {
        Image(
            painter = painterResource(R.drawable.logo_aplicativo),
            contentDescription = "",
            modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, bottom = 25.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(postagens) { post ->
                    PostCard(post)
                }
            }
        }
    }
}

@Composable
fun PostCard(post: Postagem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
    ) {
        Column (
            modifier = Modifier
                .background(Color.White)
                .padding(10.dp)
                .fillMaxWidth()
        ){
            Row() {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "",
                    modifier = Modifier.size(45.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(5.dp))
                Column {
                    Text(
                        text=post.Usuario,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text="Correu há 2 horas",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }


            }
            Spacer(Modifier.height(15.dp))
            Text(
                text=post.Distancia,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text=post.Tempo,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text="Ritmo medio: 4,5/km",
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Row() {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.ModeComment,
                        contentDescription = "Commentary",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
       }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeedScreenPreview() {
    PegaPistaTheme {
        FeedScreen()
    }
}