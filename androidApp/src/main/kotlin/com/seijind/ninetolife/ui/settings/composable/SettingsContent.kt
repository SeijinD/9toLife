package com.seijind.ninetolife.ui.settings.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seijind.ninetolife.R
import com.seijind.ninetolife.domain.settings.model.AppLanguage
import com.seijind.ninetolife.domain.settings.model.ThemeMode
import com.seijind.ninetolife.ui.common.SwipeToDeleteRow
import com.seijind.ninetolife.ui.common.format.formatDuration
import com.seijind.ninetolife.ui.settings.model.SettingsActions
import com.seijind.ninetolife.ui.settings.model.SettingsUiState
import com.seijind.ninetolife.ui.theme.AppTheme
import com.seijind.ninetolife.ui.theme.TabularFigures

private val ScreenPadding = 16.dp
private val CardPadding = 16.dp
private val SectionSpacing = 24.dp
private val SectionLabelSpacing = 8.dp
private val ItemSpacing = 12.dp
private val StepperValueWidth = 120.dp
private const val DurationValueFormat = "%d:%02d"
private const val MinutesPerHour = 60

/** [needsExactReminders] is Android-only, so it is not part of the shared state. */
@Composable
internal fun SettingsContent(
    uiState: SettingsUiState,
    needsExactReminders: Boolean,
    actions: SettingsActions,
    modifier: Modifier = Modifier,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(value = false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(state = rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = ScreenPadding)
            .padding(bottom = SectionSpacing),
        verticalArrangement = Arrangement.spacedBy(space = SectionSpacing),
    ) {
        Text(
            text = stringResource(id = R.string.settings_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        ShiftSection(uiState = uiState, actions = actions)
        RemindersSection(uiState = uiState, needsExactReminders = needsExactReminders, actions = actions)
        AppearanceSection(uiState = uiState, actions = actions)
        ExperienceSection(uiState = uiState, actions = actions)
        if (uiState.hasQr) {
            QrSection(onChangeQrClick = actions.onChangeQrClick, onDeleteQrClick = { showDeleteDialog = true })
        }
    }

    if (showDeleteDialog) {
        DeleteQrDialog(
            onConfirm = {
                showDeleteDialog = false
                actions.onDeleteQrConfirm()
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    footer: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(space = SectionLabelSpacing)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = CardPadding),
        )
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
            Column(content = content)
        }
        if (footer != null) {
            Text(
                text = footer,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = CardPadding),
            )
        }
    }
}

@Composable
private fun ShiftSection(
    uiState: SettingsUiState,
    actions: SettingsActions,
) {
    SettingsSection(
        title = stringResource(id = R.string.settings_section_shift),
        footer = stringResource(id = R.string.settings_duration_next_shift),
    ) {
        Column(
            modifier = Modifier.padding(all = CardPadding),
            verticalArrangement = Arrangement.spacedBy(space = ItemSpacing),
        ) {
            Text(text = stringResource(id = R.string.settings_duration_label), style = MaterialTheme.typography.bodyLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                RepeatingIconButton(
                    onStep = actions.onDurationDecrease,
                    enabled = uiState.canDecreaseDuration,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_remove),
                        contentDescription = stringResource(id = R.string.settings_duration_decrease_cd),
                    )
                }
                Text(
                    text = DurationValueFormat.format(
                        uiState.shiftDurationMinutes / MinutesPerHour,
                        uiState.shiftDurationMinutes % MinutesPerHour,
                    ),
                    style = MaterialTheme.typography.displaySmall.copy(fontFeatureSettings = TabularFigures),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(width = StepperValueWidth),
                )
                RepeatingIconButton(
                    onStep = actions.onDurationIncrease,
                    enabled = uiState.canIncreaseDuration,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.settings_duration_increase_cd),
                    )
                }
            }
        }
    }
}

@Composable
private fun RemindersSection(
    uiState: SettingsUiState,
    needsExactReminders: Boolean,
    actions: SettingsActions,
) {
    var showAddMenu by remember { mutableStateOf(value = false) }
    SettingsSection(title = stringResource(id = R.string.settings_reminders_label)) {
        if (uiState.reminderOffsetsMinutes.isEmpty()) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_reminders_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            )
            HorizontalDivider()
        }
        uiState.reminderOffsetsMinutes.forEach { offset ->
            val label = formatDuration(minutes = offset.toLong())
            key(offset) {
                SwipeToDeleteRow(
                    onDelete = { actions.onReminderRemove(offset) },
                    deleteLabel = stringResource(id = R.string.settings_reminder_remove_cd, label),
                ) {
                    ListItem(
                        headlineContent = { Text(text = label) },
                        leadingContent = {
                            Icon(imageVector = Icons.Filled.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        // Opaque, so the red delete background shows only while swiping.
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    )
                }
            }
            HorizontalDivider()
        }
        if (uiState.addableReminderOffsetsMinutes.isNotEmpty()) {
            Box {
                ActionRow(
                    text = stringResource(id = R.string.settings_reminder_add),
                    icon = rememberVectorPainter(image = Icons.Filled.Add),
                    onClick = { showAddMenu = true },
                )
                DropdownMenu(expanded = showAddMenu, onDismissRequest = { showAddMenu = false }) {
                    uiState.addableReminderOffsetsMinutes.forEach { offset ->
                        DropdownMenuItem(
                            text = { Text(text = formatDuration(minutes = offset.toLong())) },
                            onClick = {
                                showAddMenu = false
                                actions.onReminderAdd(offset)
                            },
                        )
                    }
                }
            }
        }
        if (needsExactReminders) {
            HorizontalDivider()
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.settings_exact_alarms_allow), color = MaterialTheme.colorScheme.primary)
                },
                supportingContent = { Text(text = stringResource(id = R.string.settings_exact_alarms_body)) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable(onClick = actions.onAllowExactRemindersClick),
            )
        }
    }
}

@Composable
private fun AppearanceSection(
    uiState: SettingsUiState,
    actions: SettingsActions,
) {
    SettingsSection(title = stringResource(id = R.string.settings_section_appearance)) {
        MenuRow(
            label = stringResource(id = R.string.settings_theme_label),
            options = ThemeMode.entries,
            selected = uiState.themeMode,
            optionLabel = { mode ->
                stringResource(
                    id = when (mode) {
                        ThemeMode.SYSTEM -> R.string.settings_theme_system
                        ThemeMode.LIGHT -> R.string.settings_theme_light
                        ThemeMode.DARK -> R.string.settings_theme_dark
                    },
                )
            },
            onSelect = actions.onThemeModeSelect,
        )
        HorizontalDivider()
        MenuRow(
            label = stringResource(id = R.string.settings_language_label),
            options = AppLanguage.entries,
            selected = uiState.language,
            optionLabel = { language ->
                stringResource(
                    id = when (language) {
                        AppLanguage.SYSTEM -> R.string.settings_language_system
                        AppLanguage.GREEK -> R.string.settings_language_greek
                        AppLanguage.ENGLISH -> R.string.settings_language_english
                    },
                )
            },
            onSelect = actions.onLanguageSelect,
        )
        HorizontalDivider()
        ActionRow(
            text = stringResource(id = R.string.settings_background_pick),
            icon = painterResource(id = R.drawable.ic_image),
            onClick = actions.onPickBackgroundClick,
        )
        if (uiState.hasBackgroundImage) {
            HorizontalDivider()
            ActionRow(
                text = stringResource(id = R.string.settings_background_reset),
                icon = painterResource(id = R.drawable.ic_undo),
                onClick = actions.onResetBackgroundClick,
            )
        }
    }
}

/** Label with the current value on the end; tapping opens the options, like an iOS menu picker. */
@Composable
private fun <T> MenuRow(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(value = false) }
    ListItem(
        headlineContent = { Text(text = label) },
        trailingContent = {
            // The menu sits in the trailing slot so it opens under the value.
            Box {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = optionLabel(selected), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = optionLabel(option)) },
                            onClick = {
                                expanded = false
                                onSelect(option)
                            },
                            trailingIcon = if (option == selected) {
                                { Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                            } else {
                                null
                            },
                        )
                    }
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable { expanded = true },
    )
}

@Composable
private fun ExperienceSection(
    uiState: SettingsUiState,
    actions: SettingsActions,
) {
    SettingsSection(title = stringResource(id = R.string.settings_section_experience)) {
        SwitchRow(
            title = stringResource(id = R.string.settings_haptics_label),
            description = null,
            checked = uiState.hapticsEnabled,
            onCheckedChange = actions.onHapticsToggle,
        )
        HorizontalDivider()
        SwitchRow(
            title = stringResource(id = R.string.settings_serious_label),
            description = stringResource(id = R.string.settings_serious_description),
            checked = uiState.seriousMode,
            onCheckedChange = actions.onSeriousModeToggle,
        )
    }
}

@Composable
private fun SwitchRow(
    title: String,
    description: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ListItem(
        headlineContent = { Text(text = title) },
        supportingContent = description?.let { { Text(text = it) } },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}

@Composable
private fun QrSection(
    onChangeQrClick: () -> Unit,
    onDeleteQrClick: () -> Unit,
) {
    SettingsSection(title = stringResource(id = R.string.settings_section_qr)) {
        ActionRow(
            text = stringResource(id = R.string.settings_qr_change),
            icon = painterResource(id = R.drawable.ic_qr),
            onClick = onChangeQrClick,
        )
        HorizontalDivider()
        ActionRow(
            text = stringResource(id = R.string.settings_qr_delete),
            icon = rememberVectorPainter(image = Icons.Filled.Delete),
            onClick = onDeleteQrClick,
            textColor = MaterialTheme.colorScheme.error,
        )
    }
}

/** A tappable row with the icon in the accent color, like an iOS Form button. [textColor] is red for destructive rows. */
@Composable
private fun ActionRow(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.primary,
) {
    ListItem(
        headlineContent = { Text(text = text, color = textColor) },
        leadingContent = { Icon(painter = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun DeleteQrDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.settings_qr_delete_title)) },
        text = { Text(text = stringResource(id = R.string.settings_qr_delete_body)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.settings_qr_delete_confirm), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.common_cancel)) }
        },
    )
}

@Composable
@Preview
@Preview(locale = "el", fontScale = 1.5f)
private fun SettingsContentPreview() {
    AppTheme(darkTheme = false) {
        SettingsContent(
            uiState = SettingsUiState(
                shiftDurationMinutes = 8 * MinutesPerHour + 30,
                reminderOffsetsMinutes = listOf(60, 30, 10),
                hasBackgroundImage = true,
                hasQr = true,
            ),
            needsExactReminders = true,
            actions = previewActions(),
        )
    }
}

private fun previewActions() = SettingsActions(
    onThemeModeSelect = {},
    onLanguageSelect = {},
    onDurationDecrease = {},
    onDurationIncrease = {},
    onReminderAdd = {},
    onReminderRemove = {},
    onAllowExactRemindersClick = {},
    onHapticsToggle = {},
    onSeriousModeToggle = {},
    onPickBackgroundClick = {},
    onResetBackgroundClick = {},
    onChangeQrClick = {},
    onDeleteQrConfirm = {},
)
