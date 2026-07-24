package com.example.matchmateapp.ui.matches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.matchmateapp.domain.Decision
import com.example.matchmateapp.domain.Match
import com.example.matchmateapp.ui.theme.MatchMateAppTheme

@Composable
fun MatchCard(match: Match, onAccept: () -> Unit, onDecline: () -> Unit, modifier: Modifier = Modifier) {
  Card(modifier = modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
      AsyncImage(
        model = match.pictureUrl,
        contentDescription = match.name,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
      )
      Text(text = match.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
      Text(text = "${match.age}, ${match.location}", style = MaterialTheme.typography.bodyMedium)

      when (match.decision) {
        Decision.NONE ->
          Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(top = 12.dp),
          ) {
            OutlinedIconButton(onClick = onDecline, shape = CircleShape) {
              Icon(Icons.Filled.Close, contentDescription = "Decline")
            }
            OutlinedIconButton(onClick = onAccept, shape = CircleShape) {
              Icon(Icons.Filled.Check, contentDescription = "Accept")
            }
          }
        Decision.ACCEPTED ->
          Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Text("Accepted")
          }
        Decision.DECLINED ->
          Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Text("Declined")
          }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun MatchCardPreview() {
  MatchMateAppTheme() {
    MatchCard(Match("1", "Florence Gagné", 43, "Keswick, Yukon", "", Decision.NONE), {}, {})
  }
}
