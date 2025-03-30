## Great Versioning Tool (GVT)

A simplified version control system for files, similar to a minimal GIT.

The system only supports files. It does not support subdirectories and does not have to (but may) support symbolic links.

### Versioning
The core unit of operation is a *version*. Each *version* contains:
- A version number (from `0` to `Integer.MAX_VALUE`)
- A commit message added when committing (`commit`) a version
- All files that were added (`add`) to GVT. Files committed in a specific version cannot be modified within that version—modifying them requires creating a new version.
- The *latest version* is the most recently created version. New versions can be created by the following commands: `init` (only version 0), `add`, `detach`, and `commit`. **Note:** These commands always operate on the latest version, not necessarily the currently checked-out version.

### Running the Application
- The system should provide an executable class `Gvt`. This class will be used to run all commands.
- The command is always the first parameter when executing the program.
- If no parameters are provided, the program should print to *System.out*: `Please specify command.` and return an error code `1`.
- If an unknown command is provided, the program should print to *System.out*: `Unknown command {specified-command}.` and return an error code `1`.

### General Rules
- All commands (except `init`) operate only in an initialized directory. If the current directory is not initialized, all other commands should print to *System.out*: `Current directory is not initialized. Please use "init" command to initialize.`, and return an error code `-2`. This error takes precedence over all others (e.g., missing file to add).
- Unless otherwise specified, if an OS-related error occurs (e.g., insufficient disk space, lack of write permissions), the program should print to *System.out*: `Underlying system problem. See ERR for details.`, return an error code `-3`, and print the stack trace to *System.err*.

### Rules for `add`, `detach`, `commit` Commands
- They have an optional parameter: `-m {"Message in quotes"}` that can be provided as the last parameter. This is a *user message*. It should be appended to the *default message* to form the version's commit message.

## Commands

### init
Initializes the GVT system in the current directory and sets both the active and latest version to 0. Commit message for version 0: `GVT initialized.`

- If the directory is already initialized, print to *System.out*: `Current directory is already initialized.`, and return error code `10`.
- If initialization succeeds, print to *System.out*: `Current directory initialized successfully.`
- The initialization should create a `.gvt` directory where all necessary system data is stored.

### add
Adds a specified file to the latest version. Also supports an optional user message (see general rules).

- If no file is specified, print to *System.out*: `Please specify file to add.`, and return error code `20`.
- If the file exists:
  - On success: Print `File added successfully. File: {file-name}` and create a new version.
  - If the file is already added: Print `File already added. File: {file-name}`.
- If the file does not exist: Print `File not found. File: {file-name}`, and return error code `21`.
- If any other error occurs: Print `File cannot be added. See ERR for details. File: {file-name}`, log the stack trace to *System.err*, and return error code `22`.

Default message: `File added successfully. File: {file-name}`.

### detach
Removes a file from the latest version without deleting it from the file system. Also supports an optional user message.

- If no file is specified, print to *System.out*: `Please specify file to detach.`, and return error code `30`.
- If the file exists:
  - On success: Print `File detached successfully. File: {file-name}` and create a new version.
  - If the file is not in GVT: Print `File is not added to GVT. File: {file-name}`.
- If any other error occurs: Print `File cannot be detached. See ERR for details. File: {file-name}`, log the stack trace to *System.err*, and return error code `31`.

Default message: `File detached successfully. File: {file-name}`.

### checkout
Restores files to the state of a specified version. This does not affect GVT tracking; for example, if a file was tracked in the restored version but not in the latest version, it should be restored but not re-tracked.

- Takes 1 parameter: the version number to restore.
- If the version number is invalid (non-existent or not a number), print to *System.out*: `Invalid version number: {specified-version}`, and return error code `60`.
- On success: Restore all files to the specified version and print `Checkout successful for version: {specified-version}`.
- In case of other errors, follow general rules.

### commit
Creates a new version in GVT with the specified file. Also supports an optional user message.

- If no file is specified, print to *System.out*: `Please specify file to commit.`, and return error code `50`.
- If the file exists:
  - If it was added and still exists, create a new version and print `File committed successfully. File: {file-name}`.
  - If it was not added, print `File is not added to GVT. File: {file-name}`.
- If the file does not exist, print `File not found. File: {file-name}`, and return error code `51`.
- If any other error occurs: Print `File cannot be committed. See ERR for details. File: {file-name}`, log the stack trace to *System.err*, and return error code `52`.

Default message: `File committed successfully. File: {file-name}`.

### history
Displays version history.

Format: `{version-number}: {commit message}` (each version appears on a new line). If the commit message is multi-line, only the first line is displayed.

- If no parameters are specified, all versions are displayed.
- The `-last {n}` parameter displays the last `n` versions.
- Invalid parameters are ignored and treated as missing.

### version
Displays details of a specific version.

- If no parameter is provided, it displays the currently active version.
- If the version number is invalid (non-existent or not a number), print to *System.out*: `Invalid version number: {specified-number}.`, and return error code `60`.

Format:
```
Version: {version-number}
{commit message}
```
