package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseCritical

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (priority) {
        Priority.LOW -> Triple(Color(0xFFE2E8F0), Color(0xFF475569), "LOW")
        Priority.MEDIUM -> Triple(Color(0xFFDBEAFE), Color(0xFF1D4ED8), "MEDIUM")
        Priority.HIGH -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "HIGH")
        Priority.CRITICAL -> Triple(Color(0xFFFFE4E6), RoseCritical, "CRITICAL")
    }

    Text(
        text = label,
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}
