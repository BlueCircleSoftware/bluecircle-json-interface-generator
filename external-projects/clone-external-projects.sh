#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage: clone-external-projects.sh [options]

Clone the repositories listed in repositories.list into the external-projects
directory. The repo inventory is intentionally separate from this script so the
script can be reused as a template for other projects.

Options:
  -f, --repo-file FILE   Repository inventory file.
                         Default: repositories.list next to this script.
  -t, --target-dir DIR   Directory to clone repositories into.
                         Default: directory containing this script.
  -u, --update           Fetch existing repositories instead of only skipping them.
  -n, --dry-run          Print the git commands without running them.
  -h, --help             Show this help.

Inventory format:
  local_path|repository_url|branch

The branch field is optional. Blank lines and lines beginning with # are ignored.
EOF
}

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
repo_file="${script_dir}/repositories.list"
target_dir="${script_dir}"
update_existing=0
dry_run=0

while [[ $# -gt 0 ]]; do
  case "$1" in
    -f|--repo-file)
      if [[ $# -lt 2 ]]; then
        echo "Missing value for $1" >&2
        exit 2
      fi
      repo_file="$2"
      shift 2
      ;;
    -t|--target-dir)
      if [[ $# -lt 2 ]]; then
        echo "Missing value for $1" >&2
        exit 2
      fi
      target_dir="$2"
      shift 2
      ;;
    -u|--update)
      update_existing=1
      shift
      ;;
    -n|--dry-run)
      dry_run=1
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 2
      ;;
  esac
done

if [[ ! -f "${repo_file}" ]]; then
  echo "Repository inventory not found: ${repo_file}" >&2
  exit 1
fi

mkdir -p "${target_dir}"

run() {
  if [[ "${dry_run}" -eq 1 ]]; then
    printf '+'
    printf ' %q' "$@"
    printf '\n'
  else
    "$@"
  fi
}

clone_repo() {
  local local_path="$1"
  local repository_url="$2"
  local branch="${3:-}"
  local destination="${target_dir}/${local_path}"

  if [[ "${local_path}" = /* || "${local_path}" = *".."* ]]; then
    echo "Refusing unsafe local path: ${local_path}" >&2
    return 1
  fi

  if [[ -d "${destination}/.git" ]]; then
    echo "Repository already exists: ${local_path}"
    local current_url
    current_url="$(git -C "${destination}" remote get-url origin 2>/dev/null || true)"
    if [[ -n "${current_url}" && "${current_url}" != "${repository_url}" ]]; then
      echo "  warning: origin URL is ${current_url}, expected ${repository_url}" >&2
    fi
    if [[ "${update_existing}" -eq 1 ]]; then
      run git -C "${destination}" fetch --prune origin
      if [[ -n "${branch}" ]]; then
        run git -C "${destination}" checkout "${branch}"
        run git -C "${destination}" pull --ff-only origin "${branch}"
      fi
    fi
    return 0
  fi

  if [[ -e "${destination}" ]]; then
    echo "Path exists but is not a git repository: ${destination}" >&2
    return 1
  fi

  if [[ -n "${branch}" ]]; then
    run git clone --branch "${branch}" "${repository_url}" "${destination}"
  else
    run git clone "${repository_url}" "${destination}"
  fi
}

while IFS='|' read -r local_path repository_url branch extra; do
  [[ -z "${local_path}" || "${local_path}" =~ ^[[:space:]]*# ]] && continue

  if [[ -n "${extra:-}" ]]; then
    echo "Invalid inventory line for ${local_path}: too many fields" >&2
    exit 1
  fi

  if [[ -z "${repository_url}" ]]; then
    echo "Invalid inventory line for ${local_path}: missing repository URL" >&2
    exit 1
  fi

  clone_repo "${local_path}" "${repository_url}" "${branch:-}"
done < "${repo_file}"
