package mx.edu.utez.jyps.data.model

/**
 * Represents a single exit-pass or justification request visible to the Department Head.
 *
 * @property id Unique identifier for this request.
 * @property employeeName Full name of the employee who submitted the request.
 * @property employeeEmail Institutional email of the employee.
 * @property requestType Whether this is a Pass or a Justification.
 * @property reason Short description / motive provided by the employee.
 * @property date Human-readable date, e.g. "27/2/2026".
 * @property time Human-readable time, e.g. "08:30 a.m.".
 * @property exitTime Optional departure time for exit passes (e.g. "10:00").
 * @property status Current lifecycle status of the request.
 * @property attachmentName File name of the supporting evidence (justifications only).
 */
data class RequestItem(
    val id: String,
    val employeeName: String,
    val employeeEmail: String,
    val requestType: RequestType,
    val reason: String,
    val date: String,
    val time: String,
    val exitTime: String? = null,
    val status: RequestStatus,
    val attachmentName: String? = null
)

/**
 * Distinguishes between the two kinds of requests a Department Head can review.
 */
enum class RequestType {
    /** Exit pass — employee leaves during work hours and may return. */
    PASS,
    /** Justification — employee explains a past absence or tardiness. */
    JUSTIFICATION
}

/**
 * Lifecycle states a request transitions through.
 */
enum class RequestStatus {
    /** Awaiting the Department Head's decision. */
    PENDING,
    /** Approved by the Department Head. */
    APPROVED,
    /** Rejected by the Department Head. */
    REJECTED,
    /** Already consumed (exit+return completed or single-use pass expired). */
    USED
}
