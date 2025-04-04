package com.example.queueview.utils.exceptions

sealed class LocationException(message: String) : Exception(message)
class LocationPermissionException : LocationException("Location permission denied")
class LocationTimeoutException : LocationException("Could not get location within timeout")
class LocationDisabledException : LocationException("Location services disabled")