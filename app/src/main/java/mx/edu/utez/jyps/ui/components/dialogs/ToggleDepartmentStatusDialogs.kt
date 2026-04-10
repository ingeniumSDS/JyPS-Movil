package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.jyps.viewmodel.DepartmentManagementViewModel

/**
 * Controller component that coordinates the display of status-related dialogs 
 * (Confirmation vs Warning) for the Department Management flow.
 *
 * @param viewModel ViewModel controlling the UI state.
 * @param onManageEmployees Navigation callback to resolve employee reassignments.
 */
@Composable
fun ToggleDepartmentStatusDialogs(
    viewModel: DepartmentManagementViewModel,
    onManageEmployees: () -> Unit
) {
    val isToggleVisible by viewModel.isStatusToggleVisible.collectAsStateWithLifecycle()
    val isWarningVisible by viewModel.isWarningVisible.collectAsStateWithLifecycle()
    val department by viewModel.selectedDept.collectAsStateWithLifecycle()
    val linkedUsers by viewModel.linkedUsers.collectAsStateWithLifecycle()

    if (isToggleVisible && department != null) {
        ConfirmToggleDialog(
            isActivating = !department!!.activo,
            onClose = viewModel::closeStatusDialogs,
            onConfirm = { viewModel.confirmToggleStatus() }
        )
    }

    if (isWarningVisible && department != null) {
        DeactivateWarningDialog(
            departmentName = department!!.nombre,
            linkedUsers = linkedUsers,
            onClose = viewModel::closeStatusDialogs,
            onManageEmployees = {
                viewModel.closeStatusDialogs()
                onManageEmployees()
            }
        )
    }
}
