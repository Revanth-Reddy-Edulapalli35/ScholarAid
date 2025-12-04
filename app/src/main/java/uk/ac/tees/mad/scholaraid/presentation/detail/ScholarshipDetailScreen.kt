package uk.ac.tees.mad.scholaraid.presentation.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.scholaraid.data.model.Scholarship

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScholarshipDetailScreen(
    navController: NavController,
    viewModel: ScholarshipDetailViewModel = hiltViewModel()
) {
    val scholarship by viewModel.scholarship.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {}
            )
        },
        bottomBar = {
            if (scholarship?.applicationLink?.isNotBlank() == true) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = viewModel::toggleSaveStatus,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isSaved) "Unsave" else "Save"
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(if (isSaved) "Saved" else "Save")
                        }

                        OutlinedButton(
                            onClick = { shareScholarship(context, scholarship) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share Scholarship"
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Share")
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = { openLink(context, scholarship?.applicationLink) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply Now")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            when (scholarship) {
                null -> CircularProgressIndicator()
                else -> ScholarshipDetailContent(scholarship = scholarship!!)
            }
        }
    }
}

@Composable
fun ScholarshipDetailContent(scholarship: Scholarship) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp), // extra bottom space for clarity
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title
        Text(
            text = scholarship.title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(thickness = 1.dp)

        // Key Details Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DetailItem(label = "Provider", value = scholarship.provider)
            DetailItem(
                label = "Amount",
                value = scholarship.amount,
                valueColor = MaterialTheme.colorScheme.secondary
            )
            DetailItem(label = "Country", value = scholarship.country)
        }

        HorizontalDivider(thickness = 1.dp)

        // Deadline
        DetailItem(
            label = "Application Deadline",
            value = scholarship.deadline,
            valueColor = MaterialTheme.colorScheme.error
        )

        HorizontalDivider(thickness = 1.dp)

        // Description
        Text(
            text = "Description:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = scholarship.description.ifBlank { "No description available." },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

fun openLink(context: Context, url: String?) {
    if (url.isNullOrBlank()) return
    val fullUrl = if (url.startsWith("http")) url else "http://$url"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
    context.startActivity(intent)
}

fun shareScholarship(context: Context, scholarship: Scholarship?) {
    if (scholarship == null) return
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_SUBJECT, "Check out this scholarship: ${scholarship.title}")
        val shareText = "I found a great scholarship: ${scholarship.title} by ${scholarship.provider}.\n" +
                "Deadline: ${scholarship.deadline}.\n" +
                "Apply here: ${scholarship.applicationLink}"
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share scholarship via"))
}
