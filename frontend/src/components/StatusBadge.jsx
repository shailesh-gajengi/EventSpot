import React from 'react';
import { Clock, CheckCircle2, XCircle } from 'lucide-react';

const StatusBadge = ({ status }) => {
  const normStatus = (status || 'Pending').toLowerCase();

  if (normStatus === 'approved' || normStatus === 'hosted') {
    return (
      <span className="status-badge approved">
        <CheckCircle2 size={13} />
        Hosted
      </span>
    );
  }

  if (normStatus === 'rejected') {
    return (
      <span className="status-badge rejected">
        <XCircle size={13} />
        Rejected
      </span>
    );
  }

  return (
    <span className="status-badge pending">
      <Clock size={13} />
      Pending Review
    </span>
  );
};

export default StatusBadge;
