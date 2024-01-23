export interface ReportDTO {
  codenarcReport: CodenarcReportDTO;
  dependencyCheckReport: DependencyCheckReportDTO;
  cpiLintReport: CPILintReportDTO;
}

export interface CodenarcReportDTO {
  summary: SummaryDTO;
  packages: PackageDTO[];
}

export interface DependencyCheckReportDTO {
  dependencies: DependencyDTO[];
}

export interface CPILintReportDTO {
  numberOfIssues: number;
  issues: IssueDTO[];
}

interface IssueDTO {
  flowId: string;
  issue: string;
}

interface DependencyDTO {
  fileName: string;
  vulnerabilities: VulnerabilitiesDTO[];
}

interface VulnerabilitiesDTO {
  name: string;
  severity: string;
}

interface SummaryDTO {
  totalFiles: number;
  filesWithViolations: number;
  priority1: number;
  priority2: number;
  priority3: number;
}

interface PackageDTO {
  path: string;
  totalFiles: number;
  filesWithViolations: number;
  priority1: number;
  priority2: number;
  priority3: number;
  files: FileDTO[];
}

interface FileDTO {
  name: string;
  violations: ViolationDTO[];
}

interface ViolationDTO {
  ruleName: string;
  priority: number;
  lineNumber: number;
  sourceLine: string;
  message: string;
}
