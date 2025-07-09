# Android - PDF Editor Suite: The All-in-One Android PDF Suite

## Project Overview

Android - PDF Editor Suite is designed to be a complete, intuitive, and high-performance solution for viewing, editing, and managing PDF documents directly on Android devices. Targeting Android 15 (SDK 35) with consideration for a minimum SDK of 26 (Android 8.0), this application aims to provide broad device compatibility while leveraging modern Android APIs. It's built for students, professionals, and general users who frequently interact with PDF files, with a strong emphasis on a responsive UI, efficient PDF rendering and manipulation, robust file handling, a seamless user experience, and **strict adherence to Android 15's privacy, security, and performance best practices.**

## Features

### A. Advanced PDF Viewer

* **Fast & Smooth Rendering:** Optimized for large PDFs, offering fluid scrolling and zooming.
    * **Android 15 Specific:** Leverages **substantial improvements to `PdfRenderer` APIs in Android 15** for enhanced performance with features like password-protected files, annotations, form editing, searching, and selection. Explores support for linearized PDF optimizations if `PdfRenderer` exposes this.
* **Multiple Viewing Modes:** Supports Single Page, Continuous Scroll (Vertical/Horizontal), and Reflow modes.
* **Dark Mode:** Provides a comfortable viewing experience in low-light environments.
* **Navigation:** Includes page thumbnails, full-text search with highlighted results, go-to-page functionality, and custom bookmarks.
* **Text Interaction:** Enables text selection and copying.
    * **Android 15 Specific:** Ensures text selection and copy features respect any new platform-level controls for data access.

### B. Comprehensive PDF Editor

* **Annotation Tools:**
    * Highlight, Underline, Strikethrough.
    * Freehand Drawing: Pens, markers, and eraser with customizable color and width.
    * Text Box insertion, basic Shapes (rectangles, circles, lines, arrows), Sticky Notes/Comments.
* **Page Management:** Reorder pages (drag-and-drop), Rotate, Delete, Insert (blank pages, image-to-PDF, pages from another PDF), Extract pages.
* **PDF Manipulation:** Merge multiple PDFs into one, Split PDF by page ranges, and apply Password Protection (encryption).
* **Signature & Stamps:** Create and add e-signatures (drawn or from an image), add custom text or image stamps.
* **Basic Text & Image Editing:** Insert images into PDFs. (Initial focus on adding new text/annotations over existing content or simple redaction, as true embedded text editing is complex).
* **Basic Form Filling:** Recognizes and allows input for simple interactive PDF form fields.
* **Android 15 Specific:** If using camera for "image-to-PDF" feature, considers the **Low Light Boost** auto-exposure mode for improved image quality in low-light conditions, and **in-app camera controls** for flash intensity.

### C. File Management & Organization

* **Intuitive File Browser:** Automatically scans and displays PDF files on the device.
* **Folder Management:** Allows users to create, rename, move, and delete folders.
* **Quick Access:** Features Recent Files and Starred/Favorite Files for quick access.
* **Standard File Operations:** Rename, Duplicate, Delete, Share, and Print documents.
* **Android 15 Specific:**
    * **Storage Access Framework (SAF):** Continues to rely heavily on SAF (including `ACTION_OPEN_DOCUMENT_TREE` for directory access) as the primary secure mechanism for accessing user-selected PDF files.
    * **Scoped Storage Compliance:** Strictly adheres to Scoped Storage principles. Avoids `MANAGE_EXTERNAL_STORAGE` permission unless absolutely critical and justifiable, preferring `MediaStore` for media-related files (if applicable for conversion) and SAF for general document access.
    * **File Integrity APIs (fs-verity):** Explores using `FileIntegrityManager` to verify the authenticity of files on disk, ensuring that a PDF hasn't been tampered with. This is highly relevant for critical document apps.
* *Stretch Goal:* Cloud Integration (Google Drive, Dropbox, OneDrive APIs) - ensuring these integrations also respect Android 15's stricter network and background access policies.

## Technical Stack & Architectural Decisions

* **Programming Language:** Kotlin
* **Target SDK:** 35
* **UI Toolkit:** Jetpack Compose. Focuses on **Compose performance best practices for Android 15** (e.g., using `remember`, `derivedStateOf`, stable keys for `LazyColumn`, avoiding backward writes, and leveraging Baseline Profiles).
* **Architecture:** MVVM (Model-View-ViewModel)
* **Concurrency:** Kotlin Coroutines and Flow. Ensures proper lifecycle-aware coroutine scope management, especially given Android 15's stricter **foreground service restrictions** for long-running background tasks. If any background file processing (e.g., merging very large PDFs, heavy image conversion) might exceed typical execution limits, considers `WorkManager` with a `mediaProcessing` foreground service type if applicable to the task, as introduced in Android 15.
* **Persistence:** Room Persistence Library
* **File Handling:** Android's Storage Access Framework (SAF) and `ContentResolver` with `content://` URIs.
* **PDF Rendering & Editing:**
    * For viewing: `androidx.pdf:pdf-viewer-fragment` (the updated Jetpack PDF viewer module).
    * For editing/manipulation: Integrates an open-source Java PDF library like **Apache PDFBox** or **OpenPDF**. The Compose UI layer will wrap and build over this library's functionalities, handling bitmap conversions for UI interaction and then saving changes back via the library.
* **Image Loading:** Coil or Glide.
* **Basic OCR:** Google ML Kit's Text Recognition API (on-device).
* **Dependency Injection:** Hilt (Dagger).
* **Build System:** Gradle (Kotlin DSL), ensuring compatibility with Android 15 SDK and potential **16KB page size support for any native libraries (e.g., in PDFBox/OpenPDF or custom NDK code)** if applicable – mentions the need to recompile with latest NDK (r26+).
* **Testing:** Unit tests (Junit, MockK), basic UI tests (Compose Test Rules).
* **Version Control:** Git.

## Project Structure (Modularization Strategy)

The project maintains a multi-module structure for better organization, scalability, and maintainability:

* `:app`: The main entry point of the application, handling navigation graph and app-level Android 15 specific setup.
* `:feature:viewer`: Contains all logic and UI components related to PDF viewing.
* `:feature:editor`: Encapsulates PDF editing tools, annotation logic, and their respective UI.
* `:feature:file_manager`: Manages local file browsing, organization, and interfaces for cloud integration, incorporating **Android 15 SAF/Storage API refinements**.
* `:data`: Handles data repositories, data sources, and Room database definitions.
* `:domain`: Contains use cases, core business logic, and common data models.
* `:common:ui`: Houses reusable Jetpack Compose UI components and application themes.
* `:common:utils`: Provides general utility functions and extension methods.
* `:pdf_engine_wrapper`: A wrapper module for the chosen PDF manipulation library, abstracting its implementation details and handling **potential Android 15 `PdfRenderer` improvements and interoperability**.

## Key Components & Class Design (High-Level)

* **`ViewerScreen` (feature:viewer):** Jetpack Compose UI for displaying PDF content.
* **`ViewerViewModel` (feature:viewer):** Manages UI state for the viewer, interacts with `PdfRendererWrapper` and `PdfRepository`.
* **`PdfRendererWrapper` (pdf_engine_wrapper):** Abstracts `PdfRenderer` APIs, handling page rendering to bitmaps, search, and text selection.
* **`AnnotationToolBar` (feature:editor):** Compose UI for various annotation tools (highlight, pen, text box).
* **`EditorScreen` (feature:editor):** Main Compose UI for PDF editing, orchestrating different editing tools.
* **`EditorViewModel` (feature:editor):** Manages editing state, interacts with `PdfManipulationUseCase` and `PdfRepository`.
* **`FileManagerScreen` (feature:file_manager):** Compose UI for browsing and managing PDF files on the device.
* **`FileManagerViewModel` (feature:file_manager):** Handles file system operations, interacts with SAF, `ContentResolver`, and `PdfRepository`.
* **`PdfRepository` (data):** Provides an abstraction layer for PDF data access, handling file I/O operations, and interacting with the `pdf_engine_wrapper`.
* **`SavePdfUseCase` (domain):** Business logic for saving modified PDFs, ensuring adherence to SAF and Android 15 storage policies.
* **`MergePdfUseCase`, `SplitPdfUseCase` (domain):** Business logic for PDF merging/splitting, utilizing the `pdf_engine_wrapper`.
* **`SecurityManager` (data/domain):** Handles password protection/encryption and potentially integrates `FileIntegrityManager` for `fs-verity`.

`ViewModel`s will be crucial for handling complex UI state that respects Android 15 lifecycle changes, ensuring data persistence across configuration changes and managing long-running operations.

## SDE Design Considerations & Challenges to Address (with Android 15 focus)

* **Performance:**
    * Optimize PDF rendering to fully leverage **Android 15's improved `PdfRenderer`**.
    * Implement efficient strategies for large file handling, including lazy loading of pages and aggressive bitmap recycling to minimize memory footprint.
* **Memory Management:**
    * Critically manage memory, especially when dealing with large PDF documents and their bitmap representations.
    * Be aware of **16KB page size support for native code** (e.g., in Apache PDFBox/OpenPDF or any custom NDK components) and thoroughly test its implications on memory alignment and performance. Recompiling native libraries with the latest NDK (r26+) might be necessary.
* **File I/O & Permissions:**
    * Strictly adhere to **Android 15's refined Scoped Storage and Storage Access Framework (SAF) behavior**. All file operations must go through SAF for user-selected files or `MediaStore` for media.
    * Properly handle `content://` URIs and associated runtime permission checks (e.g., `ContentResolver.checkUriPermission()`) for secure and compliant file access.
    * Investigate and potentially integrate **File Integrity APIs (`fs-verity`)** to verify the authenticity and detect tampering of PDF documents on disk, crucial for critical document applications.
* **Background Processing:**
    * Carefully choose between Kotlin Coroutines, Android's `WorkManager`, and the new **`mediaProcessing` foreground service type** (introduced in Android 15) for long-running PDF operations (e.g., merging very large files, complex image conversions).
    * Strictly adhere to Android 15's tighter restrictions on background activity launches and foreground services to avoid ANRs or app termination.
* **State Management:**
    * Designing robust and scalable UI state management for various editing tools and annotation layers within Jetpack Compose.
    * Optimizing for **Compose performance best practices on Android 15** to ensure a smooth and responsive user experience even with complex interactions.
* **Undo/Redo:** Designing and implementing a robust and efficient undo/redo stack for all editing operations.
* **User Experience (UX):**
    * Implementing intuitive gestures for navigation and editing.
    * Designing graceful and clear permission request flows.
    * Providing responsive UI feedback for all user interactions and background processes.
* **Security & Privacy:**
    * Ensure secure handling of sensitive PDF content, especially password-protected files.
    * Consider **partial screen sharing** implications if the app allows any form of screen sharing, ensuring only the app window's content is shared by default to prevent accidental data exposure.
    * Be mindful of **stricter intent resolution** in Android 15 and ensure explicit component declarations if exporting any `Activity` or `Service` to prevent unexpected behavior or vulnerabilities.
* **Testing & Debugging:**
    * Leverage Android 15's enhanced logging and debugging tools for identifying file access errors and performance profiling (e.g., `ProfilingManager`).
    * Thoroughly test all features, especially those interacting with new Android 15 APIs and stricter system behaviors.

## Installation and Usage

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/VishaL6i9/Android-PDF-Editor-Suite.git](https://github.com/VishaL6i9/Android-PDF-Editor-Suite.git)
    cd Android - PDF Editor Suite
    ```
2.  **Open in Android Studio:** Open the cloned project in Android Studio (ensure you have the latest stable version with Android 15 SDK support).
3.  **Sync Gradle:** Let Gradle sync the project dependencies.
4.  **Build and Run:** Select your desired emulator (running Android 15 for full feature testing) or a physical device, and click the 'Run' button in Android Studio.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Contact

For any questions or suggestions, feel free to reach out:

* **[Vishal Kandakatla]** - [vishalkandakatla@gmail.com]
