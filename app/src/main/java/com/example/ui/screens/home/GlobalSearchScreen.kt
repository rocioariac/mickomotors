package com.example.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.GarageRepository
import com.example.data.repository.SearchResultItem
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.GarageSearchBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun GlobalSearchScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  val results = remember(searchQuery) {
    if (searchQuery.isBlank()) emptyList() else repository.searchAll(searchQuery)
  }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      GarageSearchBar(
        query = searchQuery,
        onQueryChange = { searchQuery = it },
        placeholderText = "Escribe placa, cliente, repuesto, VIN, OT..."
      )

      Spacer(modifier = Modifier.height(12.dp))

      if (searchQuery.isBlank()) {
        // Quick suggestion chips
        Text(
          text = "SUGERENCIAS DE BÚSQUEDA",
          color = GarageTextMuted,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          SuggestionChip(
            onClick = { searchQuery = "V8X-321" },
            label = { Text("Placa V8X-321") },
            colors = SuggestionChipDefaults.suggestionChipColors(
              containerColor = GarageCard,
              labelColor = GarageTextPrimary
            ),
            border = SuggestionChipDefaults.suggestionChipBorder(
              enabled = true,
              borderColor = GarageBorder
            )
          )
          SuggestionChip(
            onClick = { searchQuery = "Carlos" },
            label = { Text("Carlos Mendoza") },
            colors = SuggestionChipDefaults.suggestionChipColors(
              containerColor = GarageCard,
              labelColor = GarageTextPrimary
            ),
            border = SuggestionChipDefaults.suggestionChipBorder(
              enabled = true,
              borderColor = GarageBorder
            )
          )
          SuggestionChip(
            onClick = { searchQuery = "KYB" },
            label = { Text("Amortiguador KYB") },
            colors = SuggestionChipDefaults.suggestionChipColors(
              containerColor = GarageCard,
              labelColor = GarageTextPrimary
            ),
            border = SuggestionChipDefaults.suggestionChipBorder(
              enabled = true,
              borderColor = GarageBorder
            )
          )
        }
      } else {
        Text(
          text = "RESULTADOS (${results.size})",
          color = GarageTextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (results.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = GarageTextMuted,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "No se encontraron coincidencias para \"$searchQuery\"",
                color = GarageTextSecondary,
                fontSize = 14.sp
              )
            }
          }
        } else {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
          ) {
            items(results) { item ->
              SearchResultCard(
                item = item,
                onClick = {
                  when (item.type) {
                    "VEHICLE" -> onNavigateToScreen(Screen.VehicleDetail(item.dataRef))
                    "CLIENT" -> onNavigateToScreen(Screen.ClientDetail(item.dataRef))
                    "ORDER" -> onNavigateToScreen(Screen.OrderDetail(item.dataRef))
                    "PART" -> onNavigateToScreen(Screen.PartDetail(item.dataRef))
                    "SALE" -> onNavigateToScreen(Screen.SaleDetail(item.dataRef))
                  }
                }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SearchResultCard(
  item: SearchResultItem,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = GarageCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = GarageRed.copy(alpha = 0.15f),
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Text(
              text = item.badge.uppercase(),
              color = GarageRed,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Text(
            text = item.title,
            color = GarageTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = item.subtitle,
          color = GarageTextSecondary,
          fontSize = 12.sp,
          maxLines = 1
        )
      }

      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
        contentDescription = "Abrir",
        tint = GarageTextMuted,
        modifier = Modifier.size(12.dp)
      )
    }
  }
}
