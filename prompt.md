**1. Project Overview & Scope:**

* **App Name:** ProPDF: The All-in-One Android PDF Suite
* **Target SDK:** 35 (Android 15)
* **Min SDK (Consideration):** 26 (Android 8.0) or higher, to enable broad device compatibility while focusing on modern APIs. *Note: Some Android 15 features might degrade gracefully on older versions or require conditional logic.*
* **Primary Goal:** To provide a complete, intuitive, and high-performance solution for viewing, editing, and managing PDF documents directly on Android devices.
* **Target Audience:** Students, professionals, and general users who frequently interact with PDF files.
* **Key Emphasis:** Responsive UI, efficient PDF rendering/manipulation, robust file handling, seamless user experience, and **strict adherence to Android 15's privacy, security, and performance best practices.**

---

**2. Core Functional Requirements (Features):**

**A. Advanced PDF Viewer:**
    * Fast & smooth rendering of large PDFs (scrolling, zooming).
        * **Android 15 Specific:** Leverage the **substantial improvements to `PdfRenderer` APIs in Android 15** for potentially better performance with features like password-protected files, annotations, form editing, searching, and selection. Explore support for linearized PDF optimizations if `PdfRenderer` exposes this.
    * Multiple viewing modes: Single Page, Continuous Scroll (Vertical/Horizontal), Reflow.
    * Dark Mode support.
    * Navigation: Page thumbnails, full-text search (highlighted results), go-to-page, custom bookmarks.
    * Text selection and copying.
    * **Android 15 Specific:** Ensure text selection and copy features respect any new platform-level controls for data access.

**B. Comprehensive PDF Editor:**
    * **Annotation Tools:** Highlight, Underline, Strikethrough, Freehand Drawing (pens, markers, eraser - customizable color/width), Text Box insertion, basic Shapes (rectangles, circles, lines, arrows), Sticky Notes/Comments.
    * **Page Management:** Reorder (drag-and-drop), Rotate, Delete, Insert (blank, image-to-PDF, from another PDF), Extract.
    * **PDF Manipulation:** Merge multiple PDFs, Split PDF (by page ranges), Password Protection (encryption).
    * **Signature & Stamps:** Create/add e-signatures (drawn/image), add custom text/image stamps.
    * **Basic Text & Image Editing:** Insert images. (For text editing, assume adding new text/annotations *over* existing content or simple redaction initially, as true embedded text editing is very complex).
    * **Basic Form Filling:** Recognize and allow input for simple interactive PDF form fields.
    * **Android 15 Specific:** If using camera for "image-to-PDF" feature, consider the **Low Light Boost** auto-exposure mode for improved image quality in low-light conditions, and **in-app camera controls** for flash intensity.

**C. File Management & Organization:**
    * Intuitive file browser (auto-scan PDFs on device).
    * Folder management (create, rename, move, delete).
    * Recent Files, Starred/Favorite Files.
    * Standard file operations: Rename, Duplicate, Delete, Share, Print.
    * **Android 15 Specific:**
        * **Storage Access Framework (SAF):** Continue to rely heavily on SAF (including `ACTION_OPEN_DOCUMENT_TREE` for directory access) as the primary secure mechanism for accessing user-selected PDF files.
        * **Scoped Storage Compliance:** Strictly adhere to Scoped Storage principles. Avoid `MANAGE_EXTERNAL_STORAGE` permission unless absolutely critical and justifiable, preferring `MediaStore` for media-related files (if applicable for conversion) and SAF for general document access.
        * **File Integrity APIs (fs-verity):** Explore using `FileIntegrityManager` to verify the authenticity of files on disk, ensuring that a PDF hasn't been tampered with. This is highly relevant for critical document apps.
    * *Stretch Goal:* Cloud Integration (Google Drive, Dropbox, OneDrive APIs) - ensuring these integrations also respect Android 15's stricter network and background access policies.

---

**3. Technical Stack & Architectural Decisions:**

* **Programming Language:** Kotlin.
* **Target SDK:** 35.
* **UI Toolkit:** Jetpack Compose. Focus on **Compose performance best practices for Android 15** (e.g., using `remember`, `derivedStateOf`, stable keys for `LazyColumn`, avoiding backward writes, and leveraging Baseline Profiles).
* **Architecture:** MVVM (Model-View-ViewModel).
* **Concurrency:** Kotlin Coroutines and Flow. Ensure proper lifecycle-aware coroutine scope management, especially given Android 15's stricter **foreground service restrictions** for long-running background tasks. If any background file processing (e.g., merging very large PDFs, heavy image conversion) might exceed typical execution limits, consider `WorkManager` with a `mediaProcessing` foreground service type if applicable to the task, as introduced in Android 15.
* **Persistence:** Room Persistence Library.
* **File Handling:** Android's Storage Access Framework (SAF) and `ContentResolver` with `content://` URIs.
* **PDF Rendering & Editing:**
    * For viewing: `androidx.pdf:pdf-viewer-fragment` (the updated Jetpack PDF viewer module).
    * For editing/manipulation: Integrate an open-source Java PDF library like **Apache PDFBox** or **OpenPDF**. The prompt should assume you will wrap and build a Compose UI layer over this library's functionalities, handling bitmap conversions for UI interaction and then saving changes back via the library. *Do not try to generate the full PDFBox integration code, but outline how it would conceptually fit.*
* **Image Loading:** Coil or Glide.
* **Basic OCR:** Google ML Kit's Text Recognition API (on-device).
* **Dependency Injection:** Hilt (Dagger).
* **Build System:** Gradle (Kotlin DSL), ensuring compatibility with Android 15 SDK and potential **16KB page size support for any native libraries (e.g., in PDFBox/OpenPDF or custom NDK code)** if applicable – mention the need to recompile with latest NDK (r26+).
* **Testing:** Unit tests (Junit, MockK), basic UI tests (Compose Test Rules).
* **Version Control:** Git.

---

**4. Project Structure (Modularization Strategy):**

Maintain a multi-module project structure.

* `:app` (entry point, navigation graph, app-level Android 15 specific setup)
* `:feature:viewer` (PDF viewing logic, UI)
* `:feature:editor` (PDF editing tools, annotation logic, UI)
* `:feature:file_manager` (local file Browse, management, cloud integration interfaces, **incorporating Android 15 SAF/Storage API refinements**)
* `:data` (repositories, data sources, Room database definitions)
* `:domain` (use cases, business logic, common models)
* `:common:ui` (reusable Compose UI components, themes)
* `:common:utils` (utility functions, extensions)
* `:pdf_engine_wrapper` (wrapper module for the chosen PDF manipulation library, abstracting its implementation details and handling **potential Android 15 `PdfRenderer` improvements and interoperability**).

---

**5. Key Components & Class Design (High-Level):**

For each major module/feature, outline crucial classes/composables and their responsibilities (e.g., `ViewerScreen`, `ViewerViewModel`, `PdfRendererWrapper`, `AnnotationToolBar`, `FileManagerScreen`, `PdfRepository`, `SavePdfUseCase`, etc.). Emphasize how `ViewModel`s would handle complex state that respects Android 15 lifecycle changes.

---

**6. SDE Design Considerations & Challenges to Address (with Android 15 focus):**

* **Performance:** Optimize PDF rendering for **Android 15's improved `PdfRenderer`**. Implement strategies for large file handling, lazy loading, and bitmap recycling.
* **Memory Management:** Critically manage memory, especially with large PDFs. Be aware of **16KB page size support for native code** and test implications.
* **File I/O & Permissions:**
    * Strictly adhere to **Android 15's refined Scoped Storage and SAF behavior**.
    * Properly handle `content://` URIs and associated runtime permission checks (e.g., `ContentResolver.checkUriPermission()`).
    * Investigate and potentially integrate **File Integrity APIs (`fs-verity`)** for document authenticity.
* **Background Processing:** Carefully choose between Coroutines, `WorkManager`, and new **`mediaProcessing` foreground service type** for long-running PDF operations, adhering to Android 15's tighter restrictions on background activity launches and foreground services.
* **State Management:** Complex UI state management for various editing tools and annotations within Jetpack Compose, optimizing for **Compose performance best practices on Android 15**.
* **Undo/Redo:** Designing a robust undo/redo stack.
* **User Experience (UX):** Intuitive gestures, graceful permission flows, and responsive UI feedback.
* **Security & Privacy:**
    * Ensure secure handling of sensitive PDF content (e.g., password-protected files).
    * Consider **partial screen sharing** implications if the app allows any form of screen sharing, ensuring only the app window is shared by default.
    * Be mindful of **stricter intent resolution** and ensure explicit component declarations if exporting any `Activity` or `Service`.
* **Testing & Debugging:** Leverage Android 15's enhanced logging and debugging tools for file access errors and performance profiling (e.g., `ProfilingManager`).
