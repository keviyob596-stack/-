package com.example.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.ui.MainViewModel
import com.example.ui.components.SadeeqiButton
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    viewModel: MainViewModel
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var emailOrUsername by remember { mutableStateOf("admin@sadeeqi.app") }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SadeeqiBlack)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Title & Tagline
        Text(
            text = "«صديقي»",
            color = SadeeqiYellow,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "مجتمع الألعاب والدردشة والقعدات الاجتماعية 🎮",
            color = SadeeqiGray,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SadeeqiYellow),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isRegisterMode) "إنشاء حساب جديد 🌟" else "تسجيل الدخول 👋",
                    color = SadeeqiWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (isRegisterMode) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("اسم المستخدم (Username)", color = SadeeqiGray) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SadeeqiYellow) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SadeeqiYellow,
                            unfocusedBorderColor = SadeeqiDarkBorder,
                            focusedTextColor = SadeeqiWhite,
                            unfocusedTextColor = SadeeqiWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("الاسم الظاهر (Display Name)", color = SadeeqiGray) },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = SadeeqiYellow) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SadeeqiYellow,
                            unfocusedBorderColor = SadeeqiDarkBorder,
                            focusedTextColor = SadeeqiWhite,
                            unfocusedTextColor = SadeeqiWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("البريد الإلكتروني", color = SadeeqiGray) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SadeeqiYellow) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SadeeqiYellow,
                            unfocusedBorderColor = SadeeqiDarkBorder,
                            focusedTextColor = SadeeqiWhite,
                            unfocusedTextColor = SadeeqiWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OutlinedTextField(
                        value = emailOrUsername,
                        onValueChange = { emailOrUsername = it },
                        label = { Text("البريد الإلكتروني أو اسم المستخدم", color = SadeeqiGray) },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = SadeeqiYellow) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SadeeqiYellow,
                            unfocusedBorderColor = SadeeqiDarkBorder,
                            focusedTextColor = SadeeqiWhite,
                            unfocusedTextColor = SadeeqiWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("كلمة المرور", color = SadeeqiGray) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SadeeqiYellow) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = SadeeqiGray
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SadeeqiYellow,
                        unfocusedBorderColor = SadeeqiDarkBorder,
                        focusedTextColor = SadeeqiWhite,
                        unfocusedTextColor = SadeeqiWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                SadeeqiButton(
                    text = if (isRegisterMode) "تسجيل حساب جديد" else "دخول",
                    onClick = {
                        SoundManager.playClick()
                        if (isRegisterMode) {
                            viewModel.register(username, displayName, email, password)
                        } else {
                            viewModel.login(emailOrUsername, password)
                        }
                    }
                )

                TextButton(
                    onClick = {
                        isRegisterMode = !isRegisterMode
                        SoundManager.playClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isRegisterMode) "لديك حساب بالفعل؟ سجل الدخول هنا" else "ليس لديك حساب؟ أنشئ حساباً جديداً واكسب 100 كوينز!",
                        color = SadeeqiYellow,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
