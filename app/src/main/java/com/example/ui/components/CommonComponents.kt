package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InternalAdEntity
import com.example.ui.theme.*

@Composable
fun AvatarWithFrame(
    displayName: String,
    frame: String = "NONE",
    size: Int = 48,
    modifier: Modifier = Modifier
) {
    val initial = displayName.trim().firstOrNull()?.toString() ?: "ص"
    val borderBrush = when (frame) {
        "ROYAL" -> Brush.sweepGradient(listOf(SadeeqiYellow, SadeeqiGold, SadeeqiYellowLight, SadeeqiYellow))
        "FIRE" -> Brush.linearGradient(listOf(SadeeqiRed, Color(0xFFFF5722), SadeeqiYellow))
        "GOLDEN" -> Brush.linearGradient(listOf(SadeeqiYellow, SadeeqiYellowDark))
        else -> Brush.linearGradient(listOf(SadeeqiDarkBorder, SadeeqiDarkSurface))
    }
    val borderWidth = if (frame != "NONE") 2.5.dp else 1.5.dp

    Box(
        modifier = modifier
            .size(size.dp)
            .border(borderWidth, borderBrush, CircleShape)
            .padding(if (frame != "NONE") 2.dp else 1.dp)
            .clip(CircleShape)
            .background(SadeeqiDarkSurface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = if (frame == "ROYAL" || frame == "GOLDEN") SadeeqiYellow else SadeeqiWhite,
            fontWeight = FontWeight.Bold,
            fontSize = (size * 0.42).sp
        )
        if (frame == "ROYAL") {
            Text(
                text = "👑",
                fontSize = (size * 0.3).sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-4).dp)
            )
        } else if (frame == "FIRE") {
            Text(
                text = "🔥",
                fontSize = (size * 0.3).sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-4).dp)
            )
        }
    }
}

@Composable
fun OnlineCoinChip(
    amount: Long,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SadeeqiDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiYellow.copy(alpha = 0.5f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = "🟡",
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$amount",
                color = SadeeqiYellow,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun OfflineCoinChip(
    amount: Long,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SadeeqiDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiRed.copy(alpha = 0.5f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = "🔴",
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$amount بدون نت",
                color = SadeeqiLightGray,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ConnectivityStatusBar(
    isOffline: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        color = if (isOffline) SadeeqiRedDark.copy(alpha = 0.85f) else SadeeqiDarkCard,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isOffline) SadeeqiRed else SadeeqiOnlineGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isOffline) "🔴 وضع بدون إنترنت (الألعاب والبوتات متاحة)" else "🟢 متصل بالسيرفر واللاعبين",
                    color = SadeeqiWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = if (isOffline) "تبديل لأونلاين" else "تجربة أوفلاين",
                color = SadeeqiYellow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AdBannerView(
    ads: List<InternalAdEntity>,
    onAdClick: (InternalAdEntity) -> Unit = {}
) {
    if (ads.isEmpty()) return

    val currentAd = ads.first() // Or carousel
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onAdClick(currentAd) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiYellow.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(SadeeqiYellow, SadeeqiYellowDark)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = SadeeqiBlack,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = SadeeqiYellow.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "إعلان",
                            color = SadeeqiYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentAd.title,
                        color = SadeeqiWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = currentAd.description,
                    color = SadeeqiGray,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onAdClick(currentAd) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SadeeqiYellow,
                    contentColor = SadeeqiBlack
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(
                    text = currentAd.buttonText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SadeeqiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDanger) SadeeqiRed else SadeeqiYellow,
            contentColor = if (isDanger) SadeeqiWhite else SadeeqiBlack,
            disabledContainerColor = SadeeqiDarkSurface,
            disabledContentColor = SadeeqiGray
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
