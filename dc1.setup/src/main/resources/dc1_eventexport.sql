CREATE TABLE `tblProcessLogData` (
  `ProcessId` int(19) NOT NULL,
  `SequenceId` int(10) NOT NULL,
  `PlanId` int(10) NOT NULL,
  `LocationPath` text COLLATE utf8mb4_german2_ci NOT NULL,
  `InstanceLocationId` int(10) NOT NULL,
  `DefLocationId` int(10) NOT NULL,
  `CorrelationId` int(10) NOT NULL,
  `EventId` int(10) NOT NULL,
  `SessionId` int(10) NOT NULL,
  `Sourceid` int(10) NOT NULL,
  `FaultName` varchar(255) COLLATE utf8mb4_german2_ci DEFAULT NULL,
  `AncillaryStr` text COLLATE utf8mb4_german2_ci DEFAULT NULL,
  `AncillaryInt` int(11) DEFAULT NULL,
  `EvenTime` int(19) NOT NULL,
  `DataDocument` text COLLATE utf8mb4_german2_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci;


ALTER TABLE `tblProcessLogData`
  ADD PRIMARY KEY (`ProcessId`,`SequenceId`);
COMMIT;
