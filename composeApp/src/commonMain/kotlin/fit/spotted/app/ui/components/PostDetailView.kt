package fit.spotted.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import fit.spotted.app.api.ApiClient
import fit.spotted.app.api.models.CommentData
import fit.spotted.app.emoji.ActivityType
import fit.spotted.app.ui.camera.TimerDisplay
import kotlinx.coroutines.launch
import org.kodein.emoji.compose.WithPlatformEmoji

/**
 * A reusable component that displays a post or photo detail view.
 * This component is used in both the feed and profile screens.
 */
@Composable
fun PostDetailView(
    // Image URLs
    beforeImageUrl: String,
    afterImageUrl: String,

    // Common parameters
    workoutDuration: String,
    postedAt: String,
    activityType: ActivityType,
    userName: String,

    // Optional parameters with default values
    showBeforeAfterToggle: Boolean = true,
    initialShowAfterImage: Boolean = false,

    // Likes and comments
    likes: Int = 0,
    comments: List<CommentData> = emptyList(),
    isLikedByMe: Boolean = false,

    // Action buttons (for backward compatibility)
    actionButtons: @Composable ColumnScope.() -> Unit = {},

    // Optional close action
    onClose: (() -> Unit)? = null,

    onActivityTypeClick: (() -> Unit)? = null,

    // Callback for adding a comment
    onAddComment: ((text: String) -> Unit)? = null,

    // Callback for deleting a post
    onDeletePost: (() -> Unit)? = null,

    // Post ID for comment functionality
    postId: Int? = null,

    // API client for like functionality
    apiClient: ApiClient? = null,

    // Callback for when a post is liked/unliked
    onLikeStateChanged: ((postId: Int, isLiked: Boolean) -> Unit)? = null
) {
    PostDetailViewImpl(
        workoutDuration = workoutDuration,
        postedAt = postedAt,
        activityType = activityType,
        userName = userName,
        showBeforeAfterToggle = showBeforeAfterToggle,
        initialShowAfterImage = initialShowAfterImage,
        likes = likes,
        comments = comments,
        isLikedByMe = isLikedByMe,
        actionButtons = actionButtons,
        onClose = onClose,
        onActivityTypeClick = onActivityTypeClick,
        onAddComment = onAddComment,
        onDeletePost = onDeletePost,
        postId = postId,
        apiClient = apiClient,
        onLikeStateChanged = onLikeStateChanged
    ) { showAfterImage, imageTransition ->
        // URL-based images
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = imageTransition.value * 180f
                    alpha = if (imageTransition.value > 0.5f) imageTransition.value else 1 - imageTransition.value
                },
            contentAlignment = Alignment.Center
        ) {
            // Here we would load the image from URL
            // For now, just show a placeholder
            // In a real app, you would use an image loading library
            // to load the image from the URL
            val currentUrl = if (showAfterImage) afterImageUrl else beforeImageUrl
            // Display a placeholder for now
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)
                    .graphicsLayer(
                        scaleX = if (showAfterImage) -1f else 1f
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = currentUrl,
                    contentDescription = "Post Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * A helper function to create a modern action button with an icon and a count.
 * Supports animation for a more dynamic feel.
 */
@Composable
fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    count: Int,
    tint: Color = Color.White,
    onClick: () -> Unit,
    animated: Boolean = false
) {
    // Animation for scale effect when clicked
    val scale = if (animated) {
        animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(onClick = onClick)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$count",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Private implementation of PostDetailView that handles the common UI structure.
 * This is used by both public PostDetailView functions to avoid code duplication.
 */
@Composable
private fun PostDetailViewImpl(
    // Common parameters
    workoutDuration: String,
    postedAt: String,
    activityType: ActivityType,
    userName: String,

    // Optional parameters with default values
    showBeforeAfterToggle: Boolean = true,
    initialShowAfterImage: Boolean = false,

    // Likes and comments
    likes: Int = 0,
    comments: List<CommentData> = emptyList(),
    isLikedByMe: Boolean = false,

    showLikesAndComments: Boolean = true,

    // Action buttons
    actionButtons: @Composable ColumnScope.() -> Unit = {},

    // Optional close action
    onClose: (() -> Unit)? = null,

    // Optional callback for activity type click
    onActivityTypeClick: (() -> Unit)? = null,

    // Callback for adding a comment
    onAddComment: ((text: String) -> Unit)? = null,

    // Callback for deleting a post
    onDeletePost: (() -> Unit)? = null,

    // Post ID for comment functionality
    postId: Int? = null,

    // API client for like functionality
    apiClient: ApiClient? = null,

    // Callback for when a post is liked/unliked
    onLikeStateChanged: ((postId: Int, isLiked: Boolean) -> Unit)? = null,

    // Image content - this is the part that differs between implementations
    imageContent: @Composable (showAfterImage: Boolean, imageTransition: State<Float>) -> Unit
) {
    // State for tracking which image to show (before or after)
    var showAfterImage by remember { mutableStateOf(initialShowAfterImage) }

    // State for like button, comments, and delete confirmation
    var isLiked by remember { mutableStateOf(isLikedByMe) }
    var showComments by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Use animation for smooth transitions between before/after images
    val imageTransition = animateFloatAsState(
        targetValue = if (showAfterImage) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // Call the provided image content composable
            imageContent(showAfterImage, imageTransition)
        }

        // Add TimerDisplay in the bottom-left corner, above the user info section
        TimerDisplay(
            timerText = workoutDuration,
            showPostAnimation = false,
            modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 100.dp)
        )

        // Modern overlay with user info at the bottom with animation
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(16.dp)
            ) {
                // User info with modern styling
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular profile icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colors.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colors.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = userName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable(onClick = { onActivityTypeClick?.invoke() })
                                    .padding(vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                WithPlatformEmoji(
                                    activityType.emoji
                                ) { emojiString, inlineContent ->
                                    Text(
                                        text = emojiString,
                                        inlineContent = inlineContent,
                                        fontSize = 24.sp,
                                        color = Color.White
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            // Small dot separator with modern styling
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.5f))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = postedAt,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Modern action buttons on the right side with animations
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Before/After toggle button with modern styling
                if (showBeforeAfterToggle) {
                    // Animated scale based on transition
                    val scale = animateFloatAsState(
                        targetValue = if (showAfterImage) 1.1f else 1f,
                        animationSpec = tween(300)
                    )

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.7f))
                            .clickable { showAfterImage = !showAfterImage }
                            .graphicsLayer {
                                scaleX = scale.value
                                scaleY = scale.value
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (showAfterImage) "A" else "B",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                if (showLikesAndComments) {
                    // Modern like button with animation
                    val coroutineScope = rememberCoroutineScope()
                    ActionButton(
                        icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        count = if (isLiked && !isLikedByMe) likes + 1
                        else if (!isLiked && isLikedByMe) likes - 1
                        else likes,
                        tint = if (isLiked) Color.Red else Color.White,
                        onClick = {
                            isLiked = !isLiked

                            // Notify parent component about like state change
                            postId?.let { id ->
                                onLikeStateChanged?.invoke(id, isLiked)

                                apiClient?.let { client ->
                                    coroutineScope.launch {
                                        try {
                                            if (isLiked) {
                                                client.likePost(id)
                                            } else {
                                                client.unlikePost(id)
                                            }
                                        } catch (_: Exception) {
                                            isLiked = !isLiked
                                            // Also notify about the revert
                                            onLikeStateChanged?.invoke(id, isLiked)
                                        }
                                    }
                                }
                            }
                        },
                        animated = true
                    )

                    // Modern comment button
                    ActionButton(
                        icon = Icons.Default.Email,
                        contentDescription = "Comment",
                        count = comments.size,
                        onClick = { showComments = !showComments },
                        animated = true
                    )

                    // Delete button (only shown if onDeletePost is provided)
                    onDeletePost?.let {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.7f))
                                .clickable { showDeleteConfirmation = true },
                            contentAlignment = Alignment.Center
                        ) {
                            WithPlatformEmoji(
                                "🗑️"
                            ) { emojiString, inlineContent ->
                                Text(
                                    text = emojiString,
                                    color = Color.White,
                                    inlineContent = inlineContent,
                                    fontSize = 18.sp
                                )
                            }

                        }
                    }
                }


                // If additional actionButtons are provided, use them (for backward compatibility)
                actionButtons()

                // Modern close button if needed
                if (onClose != null) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.7f))
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "×",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }

        // Modern comments overlay with animations (shown when comment button is clicked)
        AnimatedVisibility(
            visible = showComments,
            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                targetOffsetY = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .padding(24.dp)
            ) {
                Column {
                    // Modern header with title
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Comments",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White
                        )

                        // Modern close button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { showComments = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "×",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }

                    // Comments list with modern styling
                    comments.forEach { comment ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            // User avatar
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colors.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = comment.username.first().toString(),
                                    color = MaterialTheme.colors.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Comment content
                            Column {
                                Text(
                                    text = comment.username,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = comment.text,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Add comment input field
                    if (onAddComment != null) {
                        Spacer(modifier = Modifier.height(16.dp))

                        var commentText by remember { mutableStateOf("") }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Text field for comment
                            TextField(
                                value = commentText,
                                onValueChange = { commentText = it },
                                placeholder = { Text("Add a comment...", color = Color.White.copy(alpha = 0.6f)) },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.White.copy(alpha = 0.1f),
                                    cursorColor = Color.White,
                                    textColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Submit button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colors.primary)
                                    .clickable {
                                        if (commentText.isNotBlank()) {
                                            onAddComment(commentText)
                                            commentText = ""
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "→",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Post") },
                text = { Text("Are you sure you want to delete this post?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirmation = false
                            onDeletePost?.invoke()
                        }
                    ) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteConfirmation = false }
                    ) {
                        Text("Cancel")
                    }
                },
                backgroundColor = MaterialTheme.colors.surface
            )
        }
    }
}

/**
 * An overload of PostDetailView that accepts ImageBitmap images instead of URLs.
 * This is used for previewing images before posting.
 */
@Composable
fun PostDetailView(
    // ImageBitmap images
    beforeWorkoutPhoto: ImageBitmap?,
    afterWorkoutPhoto: ImageBitmap?,

    // Common parameters
    workoutDuration: String,
    postedAt: String,
    activityType: ActivityType,
    userName: String,

    // Optional parameters with default values
    showLikesAndComments: Boolean = false,

    // Optional parameters with default values
    showBeforeAfterToggle: Boolean = true,
    initialShowAfterImage: Boolean = false,

    // Likes state
    isLikedByMe: Boolean = false,

    // Action buttons (for backward compatibility)
    actionButtons: @Composable ColumnScope.() -> Unit = {},

    // Optional close action
    onClose: (() -> Unit)? = null,

    // Optional callback for activity type click
    onActivityTypeClick: (() -> Unit)? = null,

    // Callback for adding a comment
    onAddComment: ((text: String) -> Unit)? = null,

    // Callback for deleting a post
    onDeletePost: (() -> Unit)? = null,

    // Post ID for comment functionality
    postId: Int? = null,

    // API client for like functionality
    apiClient: ApiClient? = null,

    // Callback for when a post is liked/unliked
    onLikeStateChanged: ((postId: Int, isLiked: Boolean) -> Unit)? = null
) {
    // Use the common implementation with ImageBitmap image content
    PostDetailViewImpl(
        workoutDuration = workoutDuration,
        postedAt = postedAt,
        activityType = activityType,
        userName = userName,
        showBeforeAfterToggle = showBeforeAfterToggle,
        initialShowAfterImage = initialShowAfterImage,
        // No likes or comments for preview
        likes = 0,
        comments = emptyList(),
        isLikedByMe = isLikedByMe,
        showLikesAndComments = showLikesAndComments,
        actionButtons = actionButtons,
        onClose = onClose,
        onActivityTypeClick = onActivityTypeClick,
        onAddComment = onAddComment,
        onDeletePost = onDeletePost,
        postId = postId,
        apiClient = apiClient,
        onLikeStateChanged = onLikeStateChanged
    ) { showAfterImage, imageTransition ->
        // Display the ImageBitmap images directly
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = imageTransition.value * 180f
                    alpha = if (imageTransition.value > 0.5f) imageTransition.value else 1 - imageTransition.value
                },
            contentAlignment = Alignment.Center
        ) {
            // Display the current image based on showAfterImage state
            val currentPhoto = if (showAfterImage) afterWorkoutPhoto else beforeWorkoutPhoto

            // Use standard Image composable to display the ImageBitmap
            currentPhoto?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = if (showAfterImage) "After workout photo" else "Before workout photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = if (showAfterImage) -1f else 1f
                        ),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
