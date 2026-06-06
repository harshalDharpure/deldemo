package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.viewmodel.AndawalaViewModel

@Composable
fun AndawalaAppScreen(viewModel: AndawalaViewModel) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val isKannada by viewModel.isKannada.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AndawalaHeader(
                currentRole = currentRole,
                isKannada = isKannada,
                currentUser = currentUser,
                onRoleChange = { viewModel.setRole(it) },
                onLanguageToggle = { viewModel.isKannada.value = !isKannada },
                onLogout = { viewModel.logout() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (currentRole) {
                "CUSTOMER" -> CustomerPortal(viewModel, isKannada)
                "DELIVERY" -> DeliveryPartnerPortal(viewModel, isKannada)
                "ADMIN" -> AdminPortal(viewModel, isKannada)
            }
        }
    }
}

@Composable
fun AndawalaHeader(
    currentRole: String,
    isKannada: Boolean,
    currentUser: User?,
    onRoleChange: (String) -> Unit,
    onLanguageToggle: () -> Unit,
    onLogout: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: logo and switches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Logo and Title Group
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Egg,
                            contentDescription = "Logo",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Andawala",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Fresh Boiled Eggs Daily",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Translation & Role Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Switcher Button
                    IconButton(
                        onClick = onLanguageToggle,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                            .size(36.dp)
                    ) {
                        Text(
                            text = if (isKannada) "EN" else "ಕನ್ನಡ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(6.dp))

                    if (currentUser != null && currentRole == "CUSTOMER") {
                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Role Quick Swapper for Sandbox Exploration
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val roles = listOf("CUSTOMER" to "Customer", "DELIVERY" to "Delivery Partner", "ADMIN" to "Admin Tower")
                roles.forEach { (roleKey, label) ->
                    val isSelected = currentRole == roleKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { onRoleChange(roleKey) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 1. CUSTOMER PORTAL IMPLEMENTATION
// ==========================================
@Composable
fun CustomerPortal(viewModel: AndawalaViewModel, isKannada: Boolean) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    if (currentUser == null) {
        CustomerAuthScreen(viewModel, isKannada)
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab Header Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                val tabs = listOf(
                    Icons.Default.ShoppingBag to if (isKannada) "ಖರೀದಿ" else "Products",
                    Icons.Default.Autorenew to if (isKannada) "ಚಂದಾದಾರಿಕೆ" else "Subscriptions",
                    Icons.Default.Map to if (isKannada) "ಟ್ರ್ಯಾಕಿಂಗ್" else "Tracking",
                    Icons.Default.AccountBalanceWallet to if (isKannada) "ವಾಲೆಟ್" else "Wallet"
                )
                tabs.forEachIndexed { index, (icon, label) ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp)) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    0 -> CustomerCatalogView(viewModel, isKannada)
                    1 -> CustomerSubscriptionView(viewModel, isKannada)
                    2 -> CustomerTrackingView(viewModel, isKannada)
                    3 -> CustomerWalletAndProfileView(viewModel, isKannada)
                }
            }
        }
    }
}

@Composable
fun CustomerAuthScreen(viewModel: AndawalaViewModel, isKannada: Boolean) {
    val loginPhone by viewModel.loginPhone.collectAsStateWithLifecycle()
    val loginName by viewModel.loginName.collectAsStateWithLifecycle()
    val otpInput by viewModel.otpCodeInput.collectAsStateWithLifecycle()
    val isOtpSent by viewModel.isOtpSent.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EggAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isKannada) "ಅಂಡವಾಲಕ್ಕೆ ಸ್ವಾಗತ" else "Welcome to Andawala",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = if (isKannada) "ನಗರದ ಅತ್ಯಂತ ತಾಜಾ ಬೇಯಿಸಿದ ಮೊಟ್ಟೆಗಳ ವಿತರಣೆ ಸೇವೆ" else "Bengaluru's Premier Doorstep Boiled Egg Delivery",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isOtpSent) (if (isKannada) "ಒಟಿಪಿ ನಮೂದಿಸಿ" else "Enter Verification OTP") else (if (isKannada) "ದೂರವಾಣಿ ಲಾಗಿನ್" else "Mobile Number Login"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (!isOtpSent) {
                    OutlinedTextField(
                        value = loginName,
                        onValueChange = { viewModel.loginName.value = it },
                        label = { Text(if (isKannada) "ನಿಮ್ಮ ಹೆಸರು" else "Your Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = loginPhone,
                        onValueChange = { viewModel.loginPhone.value = it },
                        label = { Text(if (isKannada) "ದೂರವಾಣಿ ಸಂಖ್ಯೆ (+91)" else "Mobile Number (+91)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("phone_input"),
                        singleLine = true
                    )
                } else {
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { viewModel.otpCodeInput.value = it },
                        label = { Text(if (isKannada) "ಕೋಡ್ ನಮೂದಿಸಿ (ಟೆಂಪ್ಲೇಟ್: 1234)" else "Enter 4-Digit OTP (Hint: 1234)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("otp_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (!isOtpSent) viewModel.requestOtp() else viewModel.loginOrRegister()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_auth"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isOtpSent) (if (isKannada) "ದೃಢೀಕರಿಸಿ" else "Verify & Continue") else (if (isKannada) "ಒಟಿಪಿ ಪಡೆಯಿರಿ" else "Get OTP"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerCatalogView(viewModel: AndawalaViewModel, isKannada: Boolean) {
    val products by viewModel.allProducts.collectAsStateWithLifecycle()
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val checkoutAddress by viewModel.checkoutAddressStr.collectAsStateWithLifecycle()
    val slot by viewModel.selectedDeliverySlot.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()

    var showCartSummary by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    // Live search & category quick commerce filter logic
    val filteredProducts = products.filter { product ->
        val matchesCategory = if (selectedCategory == "ALL") true else product.category == selectedCategory
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                product.nameKannada.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Zepto/Blinkit Style Location & Estimated Delivery Time Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBike,
                            contentDescription = "Delivery Estimate",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isKannada) "೧೫ ನಿಮಿಷಗಳಲ್ಲಿ ವಿತರಣೆ" else "Delivery in 15 MINS",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFF9800))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "FAST",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 8.sp,
                                    color = Color.White
                                )
                            }
                        }
                        Text(
                            text = checkoutAddress,
                            fontSize = 11.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .clickable { /* action simulated */ }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isKannada) "ಬದಲಿ" else "Edit",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Real-time Zepto Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = if (isKannada) "ಮೊಟ್ಟೆಗಳು, ಮಸಾಲೆಗಳನ್ನು ಹುಡುಕಿ..." else "Search 'organic egg', 'combos', 'pepper'...",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Horizontal Promo Campaign Carousel Banners (Blinkit style)
            val promoState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(promoState)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Banner 1
                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .height(84.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECE5))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFE65100))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("PROTEIN", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Workout Morning Deal", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF5D4037))
                            Text("Order by 11 PM • Drop 6 AM", fontSize = 10.sp, color = Color(0xFF8D6E63))
                        }
                        Icon(Icons.Default.Egg, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(36.dp))
                    }
                }

                // Banner 2
                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .height(84.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF2E7D32))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("PRO PACK", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Andawala Club Trial", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF1B5E20))
                            Text("Get Rs. 0 delivery on next 14 orders", fontSize = 10.sp, color = Color(0xFF388E3C))
                        }
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(36.dp))
                    }
                }

                // Banner 3
                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .height(84.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFBC02D))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("COMPLIMENTARY", fontSize = 8.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Free Pepper-Salt Shaker", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF5D4037))
                            Text("With every package of 6 eggs", fontSize = 10.sp, color = Color(0xFF8D6E63))
                        }
                        Icon(Icons.Default.Eco, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(36.dp))
                    }
                }
            }

            // Rapid Category Clicker Row (Blinkit horizontal style)
            val categoryChips = listOf(
                "ALL" to (if (isKannada) "ಎಲ್ಲಾ ಸೂಪರ್" else "🛒 All Items"),
                "EGGS" to (if (isKannada) "ಬೇಯಿಸಿದ ಮೊಟ್ಟೆಗಳು" else "🥚 Boiled Eggs"),
                "ADDONS" to (if (isKannada) "ಮಸಾಲೆಗಳು" else "🧂 Add-ons"),
                "COMBOS" to (if (isKannada) "ಕಾಂಬೋಸ್" else "🍱 Meal Combos")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryChips.forEach { (catKey, label) ->
                    val isSelected = selectedCategory == catKey
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedCategory = catKey }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Products list with dynamic feedback
            if (filteredProducts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "Not Found",
                        tint = Color.Gray,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isKannada) "ಕ್ಷಮಿಸಿ, ಯಾವುದೇ ಮೊಟ್ಟೆಗಳು ಸಿಗಲಿಲ್ಲ!" else "No items matched your query!",
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Try searching something else like 'boiled' or 'pepper'.",
                        fontSize = 12.sp,
                        color = Color.Gray.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    val actualCategories = listOf("EGGS" to "Boiled Eggs Series", "ADDONS" to "Andawala Spices & Toppings", "COMBOS" to "Bengaluru Combo Packs")
                    actualCategories.forEach { (catKey, catLabel) ->
                        val sectionProducts = filteredProducts.filter { it.category == catKey }
                        if (sectionProducts.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp, 16.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = catLabel,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "${sectionProducts.size} items",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            items(sectionProducts) { product ->
                                ProductRow(
                                    product = product,
                                    quantityInCart = cart[product.id] ?: 0,
                                    isKannada = isKannada,
                                    onAdd = { viewModel.addToCart(product.id) },
                                    onRemove = { viewModel.removeFromCart(product.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Zepto/Blinkit Style Interactive Sticky Bottom Checkout Bar
        val cartCount = cart.values.sum()
        if (cartCount > 0) {
            var totalSum = 0.0
            cart.forEach { (prodId, qty) ->
                val product = products.find { it.id == prodId }
                if (product != null) {
                    totalSum += product.price * qty
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = Color(0xFF1B5E20), // Lush premium green
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .clickable { showCartSummary = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$cartCount Items",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Rs. $totalSum",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "incl. Free Pepper Shaker!",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 9.sp
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isKannada) "ಕೊಡುಗೆ ಮುಂದುವರಿಸಿ" else "View Cart Summary",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Proceed",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Cart Sheet Overlay - Styled beautifully exactly like Blinkit secure payment panel
        if (showCartSummary) {
            AlertDialog(
                onDismissRequest = { showCartSummary = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isKannada) "ನಿಮ್ಮ ಬುಟ್ಟಿ ಬಿಲ್" else "Andawala Basket Invoice",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Apartment deliver indicator
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, size = 16.dp, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Deliver at Flat 503, Sobha Tulip Apartments",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        var totalSum = 0.0
                        cart.forEach { (prodId, qty) ->
                            val product = products.find { it.id == prodId }
                            if (product != null) {
                                totalSum += product.price * qty
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isKannada) product.nameKannada else product.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Qty: $qty • Rs. ${product.price}/unit",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Text(
                                        text = "Rs. ${product.price * qty}",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Promo discount simulated line item
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery Partner Fee", fontSize = 11.sp, color = Color.Gray)
                            Text("FREE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Bill Amount",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Rs. $totalSum",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Slotted timing Selector with delivery runner bike indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, size = 16.dp, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Choose Slotted Timing Option", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val slots = listOf("Morning (6-9 AM)", "Evening (5-8 PM)")
                            slots.forEach { slotOption ->
                                val active = slot == slotOption
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(2.dp, if (active) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .background(if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                                        .clickable { viewModel.selectedDeliverySlot.value = slotOption }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = slotOption.substringBefore(" "), fontSize = 11.sp, fontWeight = FontWeight.Black)
                                        Text(text = slotOption.substringAfter("(").removeSuffix(")"), fontSize = 9.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val walletBal = user?.walletBalance ?: 0.0
                        Button(
                            onClick = {
                                viewModel.placeOrder("Wallet")
                                showCartSummary = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pay via Wallet Balance (Bal: Rs. $walletBal)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.placeOrder("Cash on Delivery")
                                showCartSummary = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cash on Delivery (Bengaluru Special)", fontSize = 11.sp)
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ProductRow(
    product: Product,
    quantityInCart: Int,
    isKannada: Boolean,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Icon representation with quick tags (Bestseller, High Protein)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (product.category == "EGGS") Icons.Default.Egg else if (product.category == "ADDONS") Icons.Default.PinDrop else Icons.Default.Fastfood,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )

                // High visual tag badge overlay
                val tag = when {
                    product.category == "EGGS" -> "PRO-FARM"
                    product.category == "ADDONS" -> "SPICY"
                    else -> "BEST VALUE"
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f))
                        .padding(vertical = 1.dp)
                ) {
                    Text(
                        text = tag,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Body text area: title, description metadata
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isKannada) product.nameKannada else product.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (product.category == "EGGS") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "12g Protein",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
                Text(
                    text = product.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Strike through price layout trick to look exactly like Blinkit/Zepto discount
                    Text(
                        text = "Rs. ${product.price}",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rs. ${product.price + 10}",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Professional ADD controller matching Blinkit and Zepto
            if (quantityInCart == 0) {
                OutlinedButton(
                    onClick = onAdd,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1B5E20)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF1B5E20)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("add_product_${product.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("ADD", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(10.dp), tint = Color(0xFF1B5E20))
                    }
                }
            } else {
                Surface(
                    color = Color(0xFF1B5E20),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = "$quantityInCart",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        IconButton(
                            onClick = onAdd,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerSubscriptionView(viewModel: AndawalaViewModel, isKannada: Boolean) {
    val planSelected by viewModel.subPlanSelected.collectAsStateWithLifecycle()
    val qtySelected by viewModel.subQuantityPerDay.collectAsStateWithLifecycle()
    val checkAddress by viewModel.checkoutAddressStr.collectAsStateWithLifecycle()
    val slot by viewModel.selectedDeliverySlot.collectAsStateWithLifecycle()
    val mySubs by viewModel.allSubscriptions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (isKannada) "ಸ್ಮಾರ್ಟ್ ಅಂಡವಾಲ ಚಂದಾದಾರಿಕೆ" else "Slotted Bangalore Doorstep Subscription",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Enjoy pre-boiled eggs delivered with automatic morning/evening routing to your Bengaluru apartment complex.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 1. Plan Frequencies
                Text(text = "Choose Subscription Cycle", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val plans = listOf("Daily" to "Daily Drop", "Weekly" to "Weekly Pack", "Monthly" to "30-Day Saver")
                    plans.forEach { (planKey, label) ->
                        val active = planSelected == planKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.subPlanSelected.value = planKey }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Quantity configuration
                Text(text = "How many boiled eggs per day?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val optionalQtys = listOf(2, 4, 6)
                    optionalQtys.forEach { count ->
                        val selected = qtySelected == count
                        IconButton(
                            onClick = { viewModel.subQuantityPerDay.value = count },
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                )
                        ) {
                            Text(
                                text = "$count",
                                fontWeight = FontWeight.Bold,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Slot Selector
                Text(text = "Delivery Slot Interval", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val slots = listOf("Morning (6-9 AM)", "Evening (5-8 PM)")
                    slots.forEach { s ->
                        val selected = s == slot
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else Color.Gray, RoundedCornerShape(8.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { viewModel.selectedDeliverySlot.value = s }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = s, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.subscribePlan() },
                    modifier = Modifier.fillMaxWidth().testTag("subscribe_action_button")
                ) {
                    Text("Subscribe & Create Auto-Scheduler")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isKannada) "ನಿಮ್ಮ ಸಕ್ರಿಯ ಚಂದಾದಾರಿಕೆಗಳು" else "Your Active Doorstep Schedules",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (mySubs.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active recurring schedules found. Subscribe above to initialize auto-doorstep drops!",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            mySubs.forEach { sub ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${sub.planName} Plan (${sub.quantityPerDay} Eggs/Day)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(text = "Slot: ${sub.deliverySlot}", fontSize = 11.sp)
                            Text(text = "Drop: ${sub.societyName}", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                text = "Status: ${sub.status}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (sub.status == "Active") Color(0xFF2E7D32) else Color.Red
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (sub.status == "Active") {
                                Button(
                                    onClick = { viewModel.pauseSubscription(sub.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Pause", fontSize = 10.sp)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.resumeSubscription(sub.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Resume", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerTrackingView(viewModel: AndawalaViewModel, isKannada: Boolean) {
    val progress by viewModel.trackerProgress.collectAsStateWithLifecycle()
    val status by viewModel.trackerStatus.collectAsStateWithLifecycle()
    val eta by viewModel.trackerEta.collectAsStateWithLifecycle()
    val locationDesc by viewModel.trackerLocationDesc.collectAsStateWithLifecycle()
    val trackingId by viewModel.liveTrackingDeliveryId.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (isKannada) "ಲೈವ್ ಬೆಂಗಳೂರು ಡೆಲಿವರಿ ಟ್ರ್ಯಾಕಿಂಗ್" else "Live Bengaluru Neighborhood Map Tracker",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Text(
            text = "Track the doorstep runner cycle live from our local Indiranagar base to your apartment Complex.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (trackingId == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBike,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No active live orders en route right now. Place a boiled egg package from the products tab to trigger active tracking!",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Active Courier Status", fontSize = 11.sp, color = Color.Gray)
                            Text(text = status, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        ) {
                            Text(
                                text = "ETA: $eta",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Current Hub Node: $locationDesc",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Society routing custom draw canvas map
                    Text(text = "Bengaluru Neighborhood Scooter Route", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8F5E9))
                            .border(1.dp, Color.LightGray)
                    ) {
                        val routeColor = MaterialTheme.colorScheme.primary
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Draw Grid Lines (Bengaluru Roads simulation)
                            drawRect(
                                color = Color.White,
                                size = size
                            )
                            
                            // Road paths
                            val pathPoints = listOf(
                                Offset(40f, 40f),
                                Offset(200f, 60f),
                                Offset(180f, 250f),
                                Offset(450f, 180f),
                                Offset(600f, 320f)
                            )
                            
                            // Draw delivery route
                            for (i in 0 until pathPoints.size - 1) {
                                drawLine(
                                    color = Color.LightGray,
                                    start = pathPoints[i],
                                    end = pathPoints[i+1],
                                    strokeWidth = 12f,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    color = routeColor,
                                    start = pathPoints[i],
                                    end = pathPoints[i+1],
                                    strokeWidth = 4f,
                                    cap = StrokeCap.Round,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                            }
                            
                            // Draw Nodes (Kitchen, flyovers, societies)
                            drawCircle(color = Color.Red, radius = 10f, center = pathPoints.first())
                            drawCircle(color = Color.Blue, radius = 10f, center = pathPoints.last())
                            
                            // Scooter location based on progress percentage
                            val pointIndex = (progress * (pathPoints.size - 1)).toInt()
                            val remainder = (progress * (pathPoints.size - 1)) - pointIndex
                            val scooterPos = if (pointIndex < pathPoints.size - 1) {
                                val sNode = pathPoints[pointIndex]
                                val eNode = pathPoints[pointIndex + 1]
                                Offset(
                                    sNode.x + (eNode.x - sNode.x) * remainder,
                                    sNode.y + (eNode.y - sNode.y) * remainder
                                )
                            } else {
                                pathPoints.last()
                            }
                            
                            drawCircle(
                                color = Color(0xFFFF9800),
                                radius = 16f,
                                center = scooterPos
                            )
                        }

                        // Labels overlay
                        Text(
                            text = "Central Kitchen",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            modifier = Modifier.padding(start = 10.dp, top = 25.dp)
                        )

                        Text(
                            text = "My Society Locked Gate",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 10.dp, bottom = 25.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerWalletAndProfileView(viewModel: AndawalaViewModel, isKannada: Boolean) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val rechargeValue by viewModel.walletRechargeAmount.collectAsStateWithLifecycle()
    val notificationLogs by viewModel.notifications.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Balance Board
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (isKannada) "ಅಂಡವಾಲ ಅಕೌಂಟ್ ಪ್ರೊಫೈಲ್" else "Andawala Secure Profile Wallet",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user?.name ?: "Indiranagar Customer",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = user?.phone ?: "",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isKannada) "ಖಾತೆ ಬಾಕಿ ವಿವರ" else "Available Wallet Balance",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
                Text(
                    text = "Rs. ${user?.walletBalance ?: 0.0}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Refill Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isKannada) "ವಾಲೆಟ್ ಹಣ ತುಂಬಿಸಿ" else "Instant Wallet Refill",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = rechargeValue,
                    onValueChange = { viewModel.walletRechargeAmount.value = it },
                    label = { Text("Recharge Value (INR)") },
                    leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("recharge_input_tag"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf("200", "500", "1000")
                    presets.forEach { amt ->
                        OutlinedButton(
                            onClick = { viewModel.walletRechargeAmount.value = amt },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+$amt", fontSize = 11.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.rechargeWallet() },
                    modifier = Modifier.fillMaxWidth().testTag("wallet_recharge_button")
                ) {
                    Text("Refill with GPay / PayTM")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Simulated Delivery alerts
        Text(
            text = "Activity Log Notifications",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (notificationLogs.isEmpty()) {
            Text(text = "No notifications yet.", fontSize = 11.sp, color = Color.Gray)
        } else {
            notificationLogs.forEach { log ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, size = 16.dp, tint = MaterialTheme.colorScheme.primary)
                            Spacer(width = 4.dp)
                            Text(text = log.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text(text = log.message, fontSize = 11.sp, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}

// Helper Extension
@Composable
private fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp, tint: Color) {
    Icon(imageVector = imageVector, contentDescription = contentDescription, modifier = Modifier.size(size), tint = tint)
}

@Composable
fun Spacer(width: androidx.compose.ui.unit.Dp) {
    Spacer(modifier = Modifier.width(width))
}

// ==========================================
// 2. DELIVERY PARTNER DASHBOARD PORTAL
// ==========================================
@Composable
fun DeliveryPartnerPortal(viewModel: AndawalaViewModel, isKannada: Boolean) {
    val deliveries by viewModel.allDeliveries.collectAsStateWithLifecycle()
    val partners by viewModel.allPartners.collectAsStateWithLifecycle()

    val assignedList = deliveries.filter { it.status == "Assigned" }
    val completedList = deliveries.filter { it.status != "Assigned" }
    val me = partners.firstOrNull() ?: DeliveryPartner(name = "Ramesh Gowda", phone = "+91 98765 43210")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Courier header card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Rider: ${me.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Bike No: ${me.bikeNo}", fontSize = 12.sp, color = Color.Gray)
                        Text(text = "Overall Rating: ⭐ ${me.rating}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Shift Cash", fontSize = 9.sp, color = Color(0xFF2E7D32))
                            Text(text = "Rs. ${me.todayEarnings}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bengaluru optimal roadmap sequencing indicator
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, size = 20.dp, tint = Color(0xFFE65100))
                Spacer(width = 8.dp)
                Text(
                    text = "Bengaluru Society Routing Optimisation Active: Dropping orders sequentially from North Hennur to South Hennur layout saves 14 mins fuel time.",
                    fontSize = 11.sp,
                    color = Color(0xFFE65100),
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Assigned Neighborhood Drops (${assignedList.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (assignedList.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                    Text(text = "All daily delivery runs finished! Outstanding job Ramesh.", fontSize = 12.sp)
                }
            }
        } else {
            assignedList.forEach { dev ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = dev.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Door: ${dev.doorNo}", fontSize = 11.sp)
                                Text(text = "Society: ${dev.societyName}, Bangalore", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                Text(text = "Phone: ${dev.phone}", fontSize = 11.sp, color = Color.Gray)
                                Text(text = "Prefer timing: ${dev.deliverySlot}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(
                                onClick = { /* simulating call dialer */ },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.markDeliveryCompleted(dev.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("Mark Delivered", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { viewModel.markCustomerUnavailable(dev.id) },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, Color.Red),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                            ) {
                                Text("Locked Gate", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Completed Drop Sheets (${completedList.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        completedList.forEach { comp ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = comp.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = comp.societyName, fontSize = 11.sp)
                        Text(text = "Timing: ${comp.actualDeliveryTime}", fontSize = 11.sp, color = Color.Gray)
                    }
                    Text(
                        text = comp.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (comp.status == "Completed") Color(0xFF2E7D32) else Color.Red
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. ADMIN PORTAL CONTROL TOWER
// ==========================================
@Composable
fun AdminPortal(viewModel: AndawalaViewModel, isKannada: Boolean) {
    val orders by viewModel.allOrders.collectAsStateWithLifecycle()
    val subscriptions by viewModel.allSubscriptions.collectAsStateWithLifecycle()
    val inventory by viewModel.inventoryRecords.collectAsStateWithLifecycle()
    val aiInsights by viewModel.aiInsights.collectAsStateWithLifecycle()
    val isPredictionLoading by viewModel.isPredictionLoading.collectAsStateWithLifecycle()

    val currentStock = inventory.firstOrNull() ?: InventoryRecord(recordDate = "2026-06-06")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Andawala Bangalore Operations Tower",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // KPI Matrix Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val totalSalesValue = orders.sumOf { it.totalAmount }
            val stats = listOf(
                "Sales Volume" to "Rs. $totalSalesValue",
                "Total Slotted Orders" to "${orders.size}",
                "Active Subscriptions" to "${subscriptions.filter { it.status == "Active" }.size}"
            )
            stats.forEach { (lbl, valStr) ->
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = lbl, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(text = valStr, fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stock and Kitchen Boiler controller
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Boiled Egg Inventory ledger", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Boiled Today", fontSize = 10.sp, color = Color.Gray)
                        Text(text = "${currentStock.eggsBoiled}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Stock Wasted", fontSize = 10.sp, color = Color.Gray)
                        Text(text = "${currentStock.eggsWasted}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Available Stock", fontSize = 10.sp, color = Color.Gray)
                        Text(text = "${currentStock.remainingStock}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { viewModel.boilEggsAdmin(150) },
                    modifier = Modifier.fillMaxWidth().testTag("admin_boil_eggs_button")
                ) {
                    Text("Boil +150 New Eggs in Kitchen")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Andawala AI predictions using Gemini
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QueryStats, contentDescription = null, size = 20.dp, tint = MaterialTheme.colorScheme.primary)
                    Spacer(width = 6.dp)
                    Text(text = "Andawala AI Demand Prediction", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(10.dp))
                
                if (isPredictionLoading) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Consulting Bengaluru weather & sub logs...", fontSize = 11.sp, color = Color.Gray)
                    }
                } else {
                    Text(
                        text = aiInsights,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.generateAiForecast() },
                    modifier = Modifier.fillMaxWidth().testTag("forecast_gemini_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Forecast with Gemini AI")
                }
            }
        }
    }
}
