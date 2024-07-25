# SeriaLog Burp Suite Extension

## Overview

SeriaLog is a Burp Suite extension that provides comprehensive logging capabilities from all Burp tools. It allows for advanced filtering using both standard filters and custom Java expressions (lambda). The extension also supports exporting all logs and the sitemap in serialized (.ser), XML, and JSON formats, ensuring data integrity and efficient storage. SeriaLog offers a user interface similar to Burp Proxy History, enabling easy highlighting, deletion, search, and other basic operations.

## Features

1. **Comprehensive Logging**:
    - Logs data from all Burp Suite tools (Proxy, Spider, Scanner, Intruder, Repeater, etc.).

2. **Advanced Filtering**:
    - Standard filtering options similar to Burp Suite's native interface.
    - Advanced filtering using Java lambda expressions for complex queries.

3. **Data Export/Import**:
    - Export logs and sitemap data in serialized (.ser), XML, and JSON formats.
    - Serialized data includes all log entries with their states, notes, and associated tools.
    - Data is compressed when using serialized format to minimize storage size.
    - Import logs from serialized (.ser), XML, and JSON files to restore previous sessions.

4. **Enhanced UI**:
    - User interface modeled after Burp Proxy History for familiarity and ease of use.
    - Supports highlighting, deletion, and search functionalities.
    - Interactive and user-friendly design.

## Installation

1. **Prerequisites**:
    - Burp Suite Professional or Community Edition.
    - Java Runtime Environment (JRE) installed.

2. **Installation Steps**:
    - Download the SeriaLog extension JAR file.
    - Open Burp Suite and navigate to the Extender tab.
    - Click on "Add" and select the downloaded JAR file.
    - SeriaLog will be loaded and visible in the Extensions list.

## Usage

1. **Logging**:
    - All actions from various Burp tools will be logged automatically.
    - Access logs from the SeriaLog tab in the Burp Suite interface.

2. **Filtering**:
    - Use the standard filter options to narrow down log entries.
    - For advanced filtering, input Java lambda expressions to create custom filters.

3. **Exporting Data**:
    - Click on the "Export" button in the SeriaLog tab.
    - Choose the desired log entries or sitemap data to export.
    - Select the format for export: serialized (.ser), XML, or JSON.
    - Save the exported data to the desired location.

4. **Importing Data**:
    - Click on the "Import" button in the SeriaLog tab.
    - Choose the file format to import: serialized (.ser), XML, or JSON.
    - Select the file containing the data to be imported.
    - The imported logs will be added to the current session.

5. **Managing Logs**:
    - Highlight log entries for easy identification.
    - Delete unnecessary logs to keep the workspace clean.
    - Use the search function to quickly find specific log entries.

## UI Components

1. **Log Panel**:
    - Displays log entries with columns for tool, URL, status, and more.
    - Supports sorting and filtering of log entries.

2. **Filter Panel**:
    - Contains standard filter options (e.g., URL, status code).
    - Advanced filter input for Java lambda expressions.

3. **Export/Import Panel**:
    - Buttons for exporting and importing log data.
    - Options for selecting specific log entries or sitemap data.
    - Supports multiple formats: serialized (.ser), XML, and JSON.

4. **Details Panel**:
    - Shows detailed information of the selected log entry.
    - Includes request and response data, notes, and other metadata.

## Export/Import Formats

- **Serialized File (.ser)**:
    - Contains all log entries with complete state, notes, and associated tool information.
    - Data is compressed to ensure minimal file size.

- **XML**:
    - Structured format suitable for data interchange and human-readable documentation.
    - Includes all relevant log entry details.

- **JSON**:
    - Lightweight data interchange format, easy to parse and generate.
    - Contains comprehensive log entry information.


