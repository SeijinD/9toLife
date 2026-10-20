package com.seijind.ninetolife.quickqr

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.seijind.ninetolife.MainActivity

/** Must match the action in res/xml/shortcuts.xml. */
internal const val ActionOpenQr = "com.seijind.ninetolife.action.OPEN_QR"

private const val OpenQrRequestCode = 0

/** Quick Settings tile that opens the app straight on the QR, which clocks in or out. */
class QuickQrTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile ?: return
        tile.state = Tile.STATE_INACTIVE
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        if (isLocked) unlockAndRun(::openQr) else openQr()
    }

    // The Intent overload is only reached below API 34, where it is the only one.
    @SuppressLint("StartActivityAndCollapseDeprecated")
    private fun openQr() {
        val intent = Intent(this, MainActivity::class.java)
            .setAction(ActionOpenQr)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(PendingIntent.getActivity(this, OpenQrRequestCode, intent, PendingIntent.FLAG_IMMUTABLE))
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
